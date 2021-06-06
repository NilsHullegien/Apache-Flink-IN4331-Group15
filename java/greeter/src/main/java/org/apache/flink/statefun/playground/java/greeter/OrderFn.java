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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.playground.java.greeter.types.Order.*;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockSubtract;
import org.apache.flink.statefun.playground.java.greeter.types.generated.UserProfile;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.io.KafkaEgressMessage;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

import static org.apache.flink.statefun.playground.java.greeter.types.Types.*;

/**
 * A simple function that computes personalized greetings messages based on a given {@link
 * UserProfile}. Then, it sends the greetings message back to the user via an egress Kafka topic.
 */
final class OrderFn implements StatefulFunction {

    private static final ValueSpec<Filing_Cabinet> FILING_CABINET = ValueSpec.named("filing_cabinet").withCustomType(Filing_Cabinet.TYPE);

    static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "order");
    static final StatefulFunctionSpec SPEC =
            StatefulFunctionSpec.builder(TYPENAME)
                    .withValueSpecs(FILING_CABINET)
                    .withSupplier(OrderFn::new)
                    .build();

    private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("order-namespace", "order");

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) {
        if (message.is(ORDER_CREATE_JSON_TYPE)) {
            System.out.println("Order Create");

            final OrderCreate orderCreateMessage = message.as(ORDER_CREATE_JSON_TYPE);

            Filing_Cabinet filing_cabinet = context.storage().get(FILING_CABINET).orElse(Filing_Cabinet.initEmpty());
            int user_id = orderCreateMessage.getUserId();

            int order_id = filing_cabinet.create(user_id);

            context.storage().set(FILING_CABINET, filing_cabinet);

            System.out.println("Order id created is: " + order_id);
        } else if (message.is(ORDER_DELETE_JSON_TYPE)) {
            System.out.println("Order Remove");

            final OrderDelete orderDeleteMessage = message.as(ORDER_DELETE_JSON_TYPE);

            Filing_Cabinet filing_cabinet = context.storage().get(FILING_CABINET).orElse(Filing_Cabinet.initEmpty());
            int order_id = orderDeleteMessage.getOrderId();

            filing_cabinet.delete(order_id);

            context.storage().set(FILING_CABINET, filing_cabinet);

            System.out.println("Order id deleted is: " + order_id);
        } else if (message.is(ORDER_FIND_JSON_TYPE)) {
            System.out.println("Find Order");

            final OrderFind orderFindMessage = message.as(ORDER_FIND_JSON_TYPE);

            Filing_Cabinet filing_cabinet = context.storage().get(FILING_CABINET).orElse(Filing_Cabinet.initEmpty());
            int order_id = orderFindMessage.getOrderId();

            Order order = filing_cabinet.getFiling_cabinet().get(order_id);

            System.out.println("Order id found: " + order_id + ", user_id: " + order.user_id + ", items: " + order.items);
        } else if (message.is(ORDER_ADD_ITEM_JSON_TYPE)) {
            System.out.println("Add Item Order");

            final OrderAddItem orderAddItemMessage = message.as(ORDER_ADD_ITEM_JSON_TYPE);

            Filing_Cabinet filing_cabinet = context.storage().get(FILING_CABINET).orElse(Filing_Cabinet.initEmpty());
            int order_id = orderAddItemMessage.getOrderId();
            int item_id = orderAddItemMessage.getItemId();

            filing_cabinet.add(order_id, item_id);

            context.storage().set(FILING_CABINET, filing_cabinet);

            System.out.println("Order: " + order_id + ", item added: " + item_id);
        } else if (message.is(ORDER_REMOVE_ITEM_JSON_TYPE)) {
            System.out.println("Remove Item Order");

            final OrderRemoveItem orderRemoveItemMessage = message.as(ORDER_REMOVE_ITEM_JSON_TYPE);

            Filing_Cabinet filing_cabinet = context.storage().get(FILING_CABINET).orElse(Filing_Cabinet.initEmpty());
            int order_id = orderRemoveItemMessage.getOrderId();
            int item_id = orderRemoveItemMessage.getItemId();

            filing_cabinet.remove(order_id, item_id);

            context.storage().set(FILING_CABINET, filing_cabinet);

            System.out.println("Order: " + order_id + ", item removed: " + item_id);
        } else if (message.is(ORDER_CHECKOUT_JSON_TYPE)) {
            System.out.println("Checkout Order");

            final OrderCheckout orderCheckoutMessage = message.as(ORDER_CHECKOUT_JSON_TYPE);

            Filing_Cabinet filing_cabinet = context.storage().get(FILING_CABINET).orElse(Filing_Cabinet.initEmpty());
            int order_id = orderCheckoutMessage.getOrderId();

            // TODO: SEND INTERNAL MESSAGE
        }
//        if (message.is(STOCK_FIND_JSON_TYPE)) {
//            System.out.println("Apply Find");
//
//            final StockFind stockFindMessage = message.as(STOCK_FIND_JSON_TYPE);
//
//            Stockroom stockroom = context.storage().get(FILING_CABINET).orElse(Stockroom.initEmpty());
//            System.out.println("ItemID: " + stockFindMessage.getItemId() + ", Quantity: " + stockroom.getStockroom().get(stockFindMessage.getItemId()).quantity);
//
////			int itemId = stockFindMessage.getItemId();
////
////			int return_stock = -42;
////			if (itemId == 1) {
////				return_stock = context.storage().get(STOCK_COUNT).orElse(-1);
////			} else if (itemId == 2) {
////				return_stock = context.storage().get(STOCK_COUNT2).orElse(-2);
////			}
////
////			System.out.printf("FIND ITEM ID: %d, STOCK: %d%n", itemId, return_stock);
//
//        } else if (message.is(STOCK_SUBTRACT_JSON_TYPE)) {
//            System.out.println("Apply Subtract");
//
//            final StockSubtract stockSubtractmessage = message.as(STOCK_SUBTRACT_JSON_TYPE);
//
//            Stockroom stockroom = context.storage().get(FILING_CABINET).orElse(Stockroom.initEmpty());
//            stockroom.add(stockSubtractmessage.getItemId(), - stockSubtractmessage.getNumber()); // minus
//
//            context.storage().set(FILING_CABINET, stockroom);
//
//        } else if (message.is(STOCK_ADD_JSON_TYPE)) {
//            System.out.println("Apply Add");
//
//            final StockAdd stockAddMessage = message.as(STOCK_ADD_JSON_TYPE);
//
////			int itemId = stockAddMessage.getItemId();
////			int itemNumber = stockAddMessage.getNumber();
////			System.out.println("ItemID: " + itemId + ", itemNumber: " + itemNumber);
////
////			if (itemId == 1) {
////				int stock_count = context.storage().get(STOCK_COUNT).orElse(0);
////				stock_count += itemNumber;
////				context.storage().set(STOCK_COUNT, stock_count);
////				System.out.println("ItemId " + itemId + " now has stock: " + stock_count);
////			} else if (itemId == 2) {
////				int stock_count = context.storage().get(STOCK_COUNT2).orElse(0);
////				stock_count += itemNumber;
////				context.storage().set(STOCK_COUNT2, stock_count);
////				System.out.println("ItemId " + itemId + " now has stock: " + stock_count);
////			}
//
//            Stockroom stockroom = context.storage().get(FILING_CABINET).orElse(Stockroom.initEmpty());
//            stockroom.add(stockAddMessage.getItemId(), stockAddMessage.getNumber());
//
//            context.storage().set(STOCKROOM, stockroom);
//
//        } else if (message.is(STOCK_ITEM_CREATE_JSON_TYPE)) {
//            System.out.println("Apply Item Create");
//
//            final StockItemCreate stockItemCreateMessage = message.as(STOCK_ITEM_CREATE_JSON_TYPE);
//
//            Stockroom stockroom = context.storage().get(STOCKROOM).orElse(Stockroom.initEmpty());
//            stockroom.create(stockItemCreateMessage.getPrice());
//
//            context.storage().set(STOCKROOM, stockroom);
//        } else {
//            throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
//        }

        return context.done();
    }

    private static class Filing_Cabinet {

        private static final ObjectMapper mapper = new ObjectMapper();

        public static final Type<Filing_Cabinet> TYPE =
                SimpleType.simpleImmutableTypeFrom(
                        TypeName.typeNameFromString("com.example/Filing_Cabinet"),
                        mapper::writeValueAsBytes,
                        bytes -> mapper.readValue(bytes, Filing_Cabinet.class));

        @JsonProperty("filing_cabinet")
        private final ArrayList<Order> filing_cabinet;

        public static Filing_Cabinet initEmpty() {
            return new Filing_Cabinet(new ArrayList<>());
        }

        @JsonCreator
        public Filing_Cabinet(@JsonProperty("filing_cabinet") ArrayList<Order> filing_cabinet) {
            this.filing_cabinet = filing_cabinet;
        }

        public int create(int user_id) {
            filing_cabinet.add(new Order(user_id));
            return filing_cabinet.size() - 1;
        }

        public void delete(int order_id) { // TODO: set checks for if null
            filing_cabinet.set(order_id, null);
        }

        public void add(int order_id, int item_id) {
            filing_cabinet.get(order_id).addItem(item_id);
        }

        public void remove(int order_id, int item_id) {
            filing_cabinet.get(order_id).removeItem(item_id);
        }

        @JsonProperty("basket")
        public ArrayList<Order> getFiling_cabinet(){
            return filing_cabinet;
        };

        public void clear() {
            filing_cabinet.clear();
        }

        @Override
        public String toString() {
            return "Basket{" + "basket=" + filing_cabinet + '}';
        }
    }

    private static class Order {
        private int user_id;
        private boolean paid;
        private HashMap<Integer, Integer> items;

        public Order(int user_id) {
            this.user_id = user_id;
            this.paid = false;
            this.items = new HashMap<>();
        }

        public Order() {

        }

        public void addItem(int item_id) { // TODO: If paid = true, dont change order anymore?
            this.items.put(item_id, this.items.getOrDefault(item_id, 0) + 1);
        }

        public void removeItem(int item_id) { // TODO: Safety check for negative values
            this.items.put(item_id, this.items.getOrDefault(item_id, 0) - 1);
        }

        public void checkOut() {
            this.paid = true;
        }

        public int getUser_id() {
            return user_id;
        }

        public boolean isPaid() {
            return paid;
        }

        public HashMap<Integer, Integer> getItems() {
            return items;
        }
    }
}
