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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;

import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.io.KafkaEgressMessage;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;
import types.Egress.EgressStockFind;
import types.Internal.InternalOrderFindCallback;
import types.Internal.InternalStockCheckoutCallback;
import types.Internal.InternalStockPollValue;
import types.Internal.InternalStockSubtract;
import types.Stock.*;

import static types.Types.*;

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
      System.out.println("Apply Stock Find");

      final StockFind stockFindMessage = message.as(STOCK_FIND_JSON_TYPE);

      Product product = getProductFromMessage(context);

      EgressStockFind egressMessage =
          new EgressStockFind(product.getQuantity(), product.getPrice());

      context.send(
          KafkaEgressMessage.forEgress(KAFKA_EGRESS)
              .withTopic("egress-stock-find")
              .withUtf8Key(String.valueOf(stockFindMessage.getUId()))
              .withValue(EGRESS_STOCK_FIND, egressMessage)
              .build());

      System.out.println("Price: " + product.getPrice() + ", Quantity: " + product.getQuantity());

    } else if (message.is(STOCK_SUBTRACT_JSON_TYPE)) { // Can go under 0
      System.out.println("Apply Stock Subtract TYPE");

      final StockSubtract stockSubtractMessage = message.as(STOCK_SUBTRACT_JSON_TYPE);
      Product product = getProductFromMessage(context);
      System.out.println("Quantity before: " + product.getQuantity());

      product.subtract(stockSubtractMessage.getNumberSubtract());
      context.storage().set(PRODUCT, product);
      System.out.println("Quantity after: " + product.getQuantity());

    } else if (message.is(STOCK_ADD_JSON_TYPE)) {
      System.out.println("Apply Stock Add");

      final StockAdd stockAddMessage = message.as(STOCK_ADD_JSON_TYPE);

      Product product = getProductFromMessage(context);

      System.out.println("Quantity before: " + product.getQuantity());
      product.add(stockAddMessage.getNumberAdd());

      System.out.println("Quantity after: " + product.getQuantity());
      context.storage().set(PRODUCT, product);

    } else if (message.is(STOCK_ITEM_CREATE_JSON_TYPE)) {
      System.out.println("Apply Stock Item Create");

      final StockItemCreate stockItemCreateMessage = message.as(STOCK_ITEM_CREATE_JSON_TYPE);
      if (!context.storage().get(PRODUCT).isPresent()) {
        System.out.println("Added new product ID to stock");
        System.out.println("PRICE: " + stockItemCreateMessage.getPrice());
        context.storage().set(PRODUCT, new Product(stockItemCreateMessage.getPrice(), 0));
      } else {
        System.out.println("Trying to add product at already existing ID");
      }

    } else if (message.is(INTERNAL_STOCK_SUBTRACT)) { // Internal message from ORDER_CHECKOUT
      System.out.println("INTERNAL STOCK SUBTRACT");
      Product product = getProductFromMessage(context);
      final InternalStockSubtract internalStockSubtractMessage =
          message.as(INTERNAL_STOCK_SUBTRACT);

      InternalStockCheckoutCallback internalCallbackMessage;

      System.out.println("Old quantity: " + product.quantity);
      product.subtract(internalStockSubtractMessage.getValue());
      System.out.println("New quantity: " + product.quantity);
      context.storage().set(PRODUCT, product);

      if (product.getQuantity() >= 0) {
        int summed_cost = internalStockSubtractMessage.getValue() * product.getPrice();
        System.out.println("Price: " + summed_cost);
        internalCallbackMessage = new InternalStockCheckoutCallback(true, summed_cost);
        System.out.println("Had enough items");

        // TODO kinda dirty hack but okay for now
      } else {
        internalCallbackMessage = new InternalStockCheckoutCallback(false, 0);
        System.out.println("Did not have enough items in stock for request");
      }

      Address caller;
      if (context.caller().isPresent()) {
        caller = context.caller().get();
      } else {
        throw new RuntimeException("CALLER NOT PRESENT");
      }

      System.out.println(
          "RETURNING Internal stock checkout callback message: "
              + "Cost: "
              + internalCallbackMessage.getSummedCost()
              + " and isOk: "
              + internalCallbackMessage.isOk());
      context.send(
          MessageBuilder.forAddress(caller)
              .withCustomType(INTERNAL_STOCK_CHECKOUT_CALLBACK, internalCallbackMessage)
              .build());
    } else if (message.is(INTERNAL_STOCK_POLL_VALUE)) {
      System.out.println("Apply stock internal poll value");

      Product product = getProductFromMessage(context);
      final InternalStockPollValue internalStockPollValueMessage =
          message.as(INTERNAL_STOCK_POLL_VALUE);

      InternalOrderFindCallback internalOrderFindCallbackMessage = new InternalOrderFindCallback(
          product.getPrice() * internalStockPollValueMessage.getCount());

      Address caller;
      if (context.caller().isPresent()) {
        caller = context.caller().get();
      } else {
        throw new RuntimeException("CALLER NOT PRESENT");
      }

      context.send(
          MessageBuilder.forAddress(caller)
              .withCustomType(INTERNAL_ORDER_FIND_CALLBACK, internalOrderFindCallbackMessage)
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
            TypeName.typeNameFromString("org.apache.flink.statefun.playground.java.greeter.example/Product"),
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
