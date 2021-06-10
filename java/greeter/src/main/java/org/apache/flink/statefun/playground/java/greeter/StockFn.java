/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.statefun.playground.java.greeter;

import static org.apache.flink.statefun.playground.java.greeter.types.Types.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.statefun.playground.java.greeter.types.Internal.InternalStockCheckoutCallback;
import org.apache.flink.statefun.playground.java.greeter.types.Internal.InternalStockSubtract;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockAdd;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockFind;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockItemCreate;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockSubtract;
import org.apache.flink.statefun.playground.java.greeter.types.generated.UserProfile;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

/**
 * A simple function that computes personalized greetings messages based on a given {@link
 * UserProfile}. Then, it sends the greetings message back to the user via an egress Kafka topic.
 */
final class StockFn implements StatefulFunction {

  private static final ValueSpec<Product> PRODUCT =
      ValueSpec.named("product").withCustomType(Product.TYPE);

  static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "stock");
  static final StatefulFunctionSpec SPEC =
      StatefulFunctionSpec.builder(TYPENAME)
          .withValueSpecs(PRODUCT)
          .withSupplier(StockFn::new)
          .build();

  private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("stock-namespace", "stock");

  @Override
  public CompletableFuture<Void> apply(Context context, Message message) {
    if (message.is(STOCK_FIND_JSON_TYPE)) {
      System.out.println("Apply Find");

      final StockFind stockFindMessage = message.as(STOCK_FIND_JSON_TYPE);

      Product product = getProductFromMessage(context);
      System.out.println("Price: " + product.price + ", Quantity: " + product.quantity);

    } else if (message.is(STOCK_SUBTRACT_JSON_TYPE)) {
      System.out.println("SUBTRACTING");

      final StockSubtract stockSubtractMessage = message.as(STOCK_SUBTRACT_JSON_TYPE);

      Product product = getProductFromMessage(context);
      product.subtract(stockSubtractMessage.getNumber());

      context.storage().set(PRODUCT, product);

    } else if (message.is(STOCK_ADD_JSON_TYPE)) {
      System.out.println("Apply Add");

      final StockAdd stockAddMessage = message.as(STOCK_ADD_JSON_TYPE);

      Product product = getProductFromMessage(context);
      product.add(stockAddMessage.getNumber());

      context.storage().set(PRODUCT, product);

    } else if (message.is(STOCK_ITEM_CREATE_JSON_TYPE)) {
      System.out.println("Apply Item Create");

      final StockItemCreate stockItemCreateMessage = message.as(STOCK_ITEM_CREATE_JSON_TYPE);
      if (!context.storage().get(PRODUCT).isPresent()) {
        context.storage().set(PRODUCT, new Product(stockItemCreateMessage.getPrice(), 0));
      }
    } else if (message.is(INTERNAL_STOCK_SUBTRACT)) {
      System.out.println("INTERNAL STOCK SUBTRACT");
      Product product = getProductFromMessage(context);
      final InternalStockSubtract internalStockSubtractMessage =
          message.as(INTERNAL_STOCK_SUBTRACT);

      InternalStockCheckoutCallback internalCallbackMessage;
      // TODO kinda dirty hack but okay for now
      product.subtract(internalStockSubtractMessage.getNumber());
      context.storage().set(PRODUCT, product);
      if (product.getQuantity() >= internalStockSubtractMessage.getNumber()) {
        internalCallbackMessage = new InternalStockCheckoutCallback(true);
      } else {
        internalCallbackMessage = new InternalStockCheckoutCallback(false);
      }

      Address caller;
      if (context.caller().isPresent()) {
        caller = context.caller().get();
      } else {
        throw new RuntimeException("CALLER NOT PRESENT");
      }

      context.send(
          MessageBuilder.forAddress(caller)
              .withCustomType(INTERNAL_STOCK_CHECKOUT_CALLBACK, internalCallbackMessage)
              .build());
    } else {
      throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
    }

    return context.done();
  }

  private Product getProductFromMessage(Context context) {
    Product product = null;
    try {
      product = context.storage().get(PRODUCT).orElseThrow(() -> new Exception("Not initialized"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return product;
  }

  private static class Product {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<Product> TYPE =
        SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.example/Product"),
            mapper::writeValueAsBytes,
            bytes -> mapper.readValue(bytes, Product.class));

    @JsonProperty("price")
    private final int price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonCreator
    public Product(@JsonProperty("price") int price, @JsonProperty("quantity") Integer quantity) {
      this.price = price;
      this.quantity = quantity;
    }

    public void add(int quantity) {
      this.quantity += quantity;
    }

    public void subtract(int quantity) {
      this.quantity -= quantity;
    }

    @Override
    public String toString() {
      return "Product{" + "price=" + price + ", quantity=" + quantity + '}';
    }

    public int getPrice() {
      return price;
    }

    public int getQuantity() {
      return quantity;
    }
  }
}
