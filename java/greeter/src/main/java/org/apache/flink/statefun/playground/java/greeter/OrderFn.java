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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.flink.statefun.playground.java.greeter.types.Internal.*;
import org.apache.flink.statefun.playground.java.greeter.types.Order.*;
import org.apache.flink.statefun.playground.java.greeter.types.Internal.InternalPaymentPay;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.StockAdd;
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
final class OrderFn implements StatefulFunction {

	private static final ValueSpec<Order> ORDER = ValueSpec.named("order").withCustomType(Order.TYPE);
	private static final ValueSpec<Integer> STOCK_POLL_OUT =
		ValueSpec.named("stock_poll_out").withIntType();
	private static final ValueSpec<Boolean> RESTOCK_SENT =
		ValueSpec.named("restock_sent").withBooleanType();
	private static final ValueSpec<Boolean> IS_PAYING =
		ValueSpec.named("is_paying").withBooleanType();

	private static final ValueSpec<Integer> ORDER_COST = ValueSpec.named("order_cost").withIntType();

	static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "order");
	static final StatefulFunctionSpec SPEC =
		StatefulFunctionSpec.builder(TYPENAME)
			.withValueSpecs(ORDER, STOCK_POLL_OUT, RESTOCK_SENT, ORDER_COST, IS_PAYING)
			.withSupplier(OrderFn::new)
			.build();

	private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("order-namespace", "order");

	@Override
	public CompletableFuture<Void> apply(Context context, Message message) {
		System.out.println("IN CHECKOUT MODE: " + inCheckoutMode(context));
		if (!inCheckoutMode(context)) { // Accept external messages

			if (message.is(ORDER_CREATE_JSON_TYPE)) {
				System.out.println("Apply Order Create");

				final OrderCreate orderCreateMessage = message.as(ORDER_CREATE_JSON_TYPE);
				if (!context.storage().get(ORDER).isPresent()) {
					context.storage().set(ORDER, new Order(orderCreateMessage.getUserId()));
					System.out.println("Created order");
				} else {
					System.out.println("Order already existed");
				}

			} else if (message.is(ORDER_DELETE_JSON_TYPE)) {
				System.out.println("Apply Order Remove");

				final OrderDelete orderDeleteMessage = message.as(ORDER_DELETE_JSON_TYPE);

				Order order = getOrderFromMessage(context);
				order.delete();

				context.storage().set(ORDER, order);

			} else if (message.is(ORDER_FIND_JSON_TYPE)) {
				System.out.println("Find Order");

				final OrderFind orderFindMessage = message.as(ORDER_FIND_JSON_TYPE);

				Order order = getOrderFromMessage(context);
				System.out.println(order.toString());
				//Total cost to Stock

			} else if (message.is(ORDER_ADD_ITEM_JSON_TYPE)) {
				System.out.println("Add Order Item");

				final OrderAddItem orderAddItemMessage = message.as(ORDER_ADD_ITEM_JSON_TYPE);

				Order order = getOrderFromMessage(context);

				System.out.println("Before: " + order.items.toString());
				order.add(orderAddItemMessage.getItemId());
				System.out.println("After: " + order.items.toString());

				context.storage().set(ORDER, order);

			} else if (message.is(ORDER_REMOVE_ITEM_JSON_TYPE)) {
				System.out.println("Apply Order Remove Item");

				final OrderRemoveItem orderRemoveItemMessage = message.as(ORDER_REMOVE_ITEM_JSON_TYPE);

				Order order = getOrderFromMessage(context);

				System.out.println("Before: " + order.items.toString());
				order.remove(orderRemoveItemMessage.getItemId());
				System.out.println("After: " + order.items.toString());

				context.storage().set(ORDER, order);

			} else if (message.is(ORDER_CHECKOUT_JSON_TYPE)) {
				System.out.println("Apply Order Checkout");

				context.storage().set(ORDER_COST, 0);

				int stock_poll_out = context.storage().get(STOCK_POLL_OUT).orElse(0);

				if (stock_poll_out == 0) { // might be unnecessary?
					Order order = getOrderFromMessage(context);

					int order_id = orderCheckoutMessage.getOrderId();

					stock_poll_out = order.items.size();

					context.storage().set(STOCK_POLL_OUT, stock_poll_out);
					context.storage().set(RESTOCK_SENT, false);

					System.out.println("Sending stock requests to " + stock_poll_out + " items");

					for (Map.Entry<Integer, Integer> item : order.items.entrySet()) {
						final InternalStockSubtract internalSubtractMessage =
								new InternalStockSubtract(item.getValue());
						context.send(
								MessageBuilder.forAddress(StockFn.TYPENAME, item.getKey().toString())
										.withCustomType(INTERNAL_STOCK_SUBTRACT, internalSubtractMessage)
										.build());
					}
				}

			} else if (message.is(INTERNAL_ORDER_IS_PAID)) {
				System.out.println("Apply Order Internal Is Paid");
				Order order = getOrderFromMessage(context);
				System.out.println("ORDERFN: ORDER IS PAID: " + order.isPaid());

				//TODO Cannot change order if ispaid = true

				//TODO Egress to server with paid = true/false
			} else if (message.is(INTERNAL_PAYMENT_CANCEL)) {  // Can be used internally when checkout fails, not using this one
				System.out.println("APPLY ORDER INTERNAL PAYMENT CANCEL --- DO NOT USE");
//				Order order = getOrderFromMessage(context);
//				order.setPaid(false);
//				context.storage().set(ORDER, order);
//
//				Address caller;
//				if (context.caller().isPresent()) {
//					caller = context.caller().get();
//				} else {
//					throw new RuntimeException("CALLER NOT PRESENT");
//				}
//				//GET TOTAL COST OF ORDER

			} else {
				System.out.println("Couldnt identify message in Order (non checkout mode): " + message.valueTypeName().asTypeNameString());
			}

		} else { // IN CHECKOUT MODE
			if (message.is(INTERNAL_STOCK_CHECKOUT_CALLBACK)) {
				System.out.println("Apply Order Internal Checkout Callback");

				final InternalStockCheckoutCallback internalMessage =
						message.as(INTERNAL_STOCK_CHECKOUT_CALLBACK);
				int stockPollOut = getStockPollOut(context) - 1;
				context.storage().set(STOCK_POLL_OUT, stockPollOut);

				context.storage().set(ORDER_COST, context.storage().get(ORDER_COST).orElse(0) + internalMessage.getSummed_cost());

				if (internalMessage.isOk()) {
					if (stockPollOut == 0
							&& !isRestockSent(context)) { // Last message was received, we can stop now
						System.out.println("All stock has been approved");

						Order order = getOrderFromMessage(context);

						context.storage().set(IS_PAYING, true);
						final InternalPaymentPay paymentPayMessage = new InternalPaymentPay(context.storage().get(ORDER_COST).get());
						context.send(
								MessageBuilder.forAddress(PaymentFn.TYPENAME, Integer.toString(order.userId))
										.withCustomType(INTERNAL_PAYMENT_PAY_JSON_TYPE, paymentPayMessage)
										.build());

						// TODO receive status message back from PaymentFn
						// TODO clear items ?? only when payment is complete
						// Are we sure? Order != Basket
					}
				} else { // Error has occured, we need to fix the stock
					if (!isRestockSent(context)) {
						System.out.println("At least one stock was low on supply");
						context.storage().set(RESTOCK_SENT, true);

						Order order = getOrderFromMessage(context);
						// TODO failed to webserver EGRESS
						for (Map.Entry<Integer, Integer> item : order.items.entrySet()) {
							final StockAdd internalAddMessage = new StockAdd(item.getValue());
							context.send(
									MessageBuilder.forAddress(StockFn.TYPENAME, item.getKey().toString())
											.withCustomType(STOCK_ADD_JSON_TYPE, internalAddMessage)
											.build());
						}
					}
				}

			} else if (message.is(INTERNAL_ORDER_PAY)) {
				InternalOrderPay internalOrderPayMessage = message.as(INTERNAL_ORDER_PAY);

				context.storage().set(IS_PAYING, false);
				if (internalOrderPayMessage.isPaid()) {
					System.out.println("Payment successful");
					// TODO EGRESS SUCCESS
				} else {
					System.out.println("Payment failed, restocking now");

					Order order = getOrderFromMessage(context);
					// TODO failed to webserver EGRESS

					for (Map.Entry<Integer, Integer> item : order.items.entrySet()) {
						final StockAdd internalAddMessage = new StockAdd(item.getValue());
						context.send(
								MessageBuilder.forAddress(StockFn.TYPENAME, item.getKey().toString())
										.withCustomType(STOCK_ADD_JSON_TYPE, internalAddMessage)
										.build());
					}
				}

			} else {
				throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
			}
		}

		return context.done();
	}

	private Order getOrderFromMessage(Context context) {
		Order order = null;
		try {
			order =
				context.storage().get(ORDER).orElseThrow(() -> new Exception("Order not initialized?"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return order;
	}

	private Integer getStockPollOut(Context context) {
		Integer stockPollOut = -1;
		try {
			stockPollOut = context.storage().get(STOCK_POLL_OUT).orElse(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stockPollOut;
	}

	private Boolean isRestockSent(Context context) {
		Boolean isRestockSent = false;
		try {
			isRestockSent =
				context
					.storage()
					.get(RESTOCK_SENT)
					.orElseThrow(() -> new Exception("Restock sent does not exist yet"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isRestockSent;
	}

	private Boolean inCheckoutMode(Context context) {
		return (getStockPollOut(context) > 0 || isPaying(context));
	}

	private static class Order {

		private static final ObjectMapper mapper = new ObjectMapper();

		public static final Type<Order> TYPE =
			SimpleType.simpleImmutableTypeFrom(
				TypeName.typeNameFromString("com.example/Order"),
				mapper::writeValueAsBytes,
				bytes -> mapper.readValue(bytes, Order.class));

		@JsonProperty("user_id")
		private final int userId;

		@JsonProperty("items")
		private final HashMap<Integer, Integer> items;

		@JsonProperty("paid")
		private boolean hasPaid;

		@JsonProperty("is_deleted")
		private boolean isDeleted;

		@Override
		public String toString() {
			return "Order{"
				+ "userId="
				+ userId
				+ ", items="
				+ items
				+ ", hasPaid="
				+ hasPaid
				+ ", isDeleted="
				+ isDeleted
				+ '}';
		}

		@JsonCreator
		public Order(@JsonProperty("user_id") int userId) {
			this.userId = userId;
			this.hasPaid = false;
			this.isDeleted = false;
			this.items = new HashMap<>();
		}

		public void add(int item_id) {
			items.put(item_id, items.getOrDefault(item_id, 0) + 1);
		}

		public void remove(int item_id) {
			items.computeIfPresent(item_id, (k, v) -> v != 0 ? v - 1 : v);
		}

		public void delete() {
			this.isDeleted = true;
		}

		public void checkOut() {
			this.hasPaid = true;
		}

		public boolean isPaid() {
			return this.hasPaid;
		}
	}
}
