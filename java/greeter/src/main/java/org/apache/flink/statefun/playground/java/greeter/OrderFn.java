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
import org.apache.flink.statefun.playground.java.greeter.types.Internal.*;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockAdd;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockItemCreate;
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

    private static final ValueSpec<Order> ORDER = ValueSpec.named("order").withCustomType(Order.TYPE);
    private static final ValueSpec<Integer> STOCK_POLL_OUT = ValueSpec.named("stock_poll_out").withIntType();
    private static final ValueSpec<Boolean> RESTOCK_SENT = ValueSpec.named("restock_sent").withBooleanType();

    static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "order");
    static final StatefulFunctionSpec SPEC =
            StatefulFunctionSpec.builder(TYPENAME)
                    .withValueSpecs(ORDER, STOCK_POLL_OUT, RESTOCK_SENT)
                    .withSupplier(OrderFn::new)
                    .build();

    private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("order-namespace", "order");

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) {
        System.out.println("IN CHECKOUT MODE: " + inCheckoutMode(context));
        if (!inCheckoutMode(context)) { //Accept external messages

            if (message.is(ORDER_CREATE_JSON_TYPE)) {
                System.out.println("Order Create");

                final OrderCreate orderCreateMessage = message.as(ORDER_CREATE_JSON_TYPE);
                if (!context.storage().get(ORDER).isPresent()) {
                    context.storage().set(ORDER, new Order(orderCreateMessage.getUserId()));
                }

            } else if (message.is(ORDER_DELETE_JSON_TYPE)) {
                System.out.println("Order Remove");

                final OrderDelete orderDeleteMessage = message.as(ORDER_DELETE_JSON_TYPE);

                Order order = getOrderFromMessage(context);
                order.delete();

                context.storage().set(ORDER, order);

            } else if (message.is(ORDER_FIND_JSON_TYPE)) {
                System.out.println("Find Order");

                final OrderFind orderFindMessage = message.as(ORDER_FIND_JSON_TYPE);

                Order order = getOrderFromMessage(context);
                System.out.println(order.toString());


            } else if (message.is(ORDER_ADD_ITEM_JSON_TYPE)) {
                System.out.println("Add Item Order");

                final OrderAddItem orderAddItemMessage = message.as(ORDER_ADD_ITEM_JSON_TYPE);

                Order order = getOrderFromMessage(context);

                order.add(orderAddItemMessage.getItemId());

                context.storage().set(ORDER, order);

            } else if (message.is(ORDER_REMOVE_ITEM_JSON_TYPE)) {
                System.out.println("Remove Item Order");

                final OrderRemoveItem orderRemoveItemMessage = message.as(ORDER_REMOVE_ITEM_JSON_TYPE);

                Order order = getOrderFromMessage(context);

                order.remove(orderRemoveItemMessage.getItemId());

                context.storage().set(ORDER, order);

            } else if (message.is(ORDER_CHECKOUT_JSON_TYPE)) {
                System.out.println("Checkout Order");

                int stock_poll_out = context.storage().get(STOCK_POLL_OUT).orElse(0);

                if (stock_poll_out == 0) {
                    final OrderCheckout orderCheckoutMessage = message.as(ORDER_CHECKOUT_JSON_TYPE);

                    Order order = getOrderFromMessage(context);

                    int order_id = orderCheckoutMessage.getOrderId();

                    stock_poll_out = order.items.size();

                    context.storage().set(STOCK_POLL_OUT, stock_poll_out);
                    context.storage().set(RESTOCK_SENT, false);

                    for (Map.Entry<Integer, Integer> item : order.items.entrySet()) {
                        final InternalStockSubtract internalSubtractMessage = new InternalStockSubtract(item.getValue());
                        context.send(
                                MessageBuilder.forAddress(StockFn.TYPENAME, item.getKey().toString())
                                        .withCustomType(INTERNAL_STOCK_SUBTRACT, internalSubtractMessage)
                                        .build());
                    }
                } else {
                    System.out.println("Checkout still waiting for stock callback");
                }
            }
        } else { // IN CHECKOUT MODE
            if (message.is(INTERNAL_STOCK_CHECKOUT_CALLBACK)) {
                final InternalStockCheckoutCallback internalMessage = message.as(INTERNAL_STOCK_CHECKOUT_CALLBACK);
                int stockPollOut = getStockPollOut(context) - 1;
                context.storage().set(STOCK_POLL_OUT, stockPollOut);

                if (internalMessage.isOk()) {
                    if (stockPollOut == 0 && !isRestockSent(context)) { //Last message was received, we can stop now
                        System.out.println("All stock has been approved");
                        //TODO payment

                        //TODO clear items
                    }
                } else { //Error has occured, we need to fix the stock
                    if (!isRestockSent(context)) {
                        context.storage().set(RESTOCK_SENT, true);

                        Order order = getOrderFromMessage(context);
                        //TODO failed to webserver

                        for (Map.Entry<Integer, Integer> item : order.items.entrySet()) {
                            final StockAdd internalAddMessage = new StockAdd(item.getValue());
                            context.send(
                                    MessageBuilder.forAddress(StockFn.TYPENAME, item.getKey().toString())
                                            .withCustomType(STOCK_ADD_JSON_TYPE, internalAddMessage)
                                            .build());
                        }
                    }
                }

            } else {
                throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
            }
        }


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
