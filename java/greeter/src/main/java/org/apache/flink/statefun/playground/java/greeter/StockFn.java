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
import org.apache.flink.statefun.playground.java.greeter.types.StockAdd;
import org.apache.flink.statefun.playground.java.greeter.types.StockFind;
import org.apache.flink.statefun.playground.java.greeter.types.StockItemCreate;
import org.apache.flink.statefun.playground.java.greeter.types.StockSubtract;
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
final class StockFn implements StatefulFunction {

	private static final ValueSpec<Integer> STOCK_COUNT = ValueSpec.named("stock_count").withIntType();
	private static final ValueSpec<Integer> STOCK_COUNT2 = ValueSpec.named("stock_count2").withIntType();
	private static final ValueSpec<Stockroom> STOCKROOM = ValueSpec.named("stockroom").withCustomType(Stockroom.TYPE);

	static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "stock");
	static final StatefulFunctionSpec SPEC =
		StatefulFunctionSpec.builder(TYPENAME)
			.withValueSpecs(STOCK_COUNT, STOCK_COUNT2, STOCKROOM)
			.withSupplier(StockFn::new)
			.build();

	private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("stock-namespace", "stock");

	@Override
	public CompletableFuture<Void> apply(Context context, Message message) {
		if (message.is(STOCK_FIND_JSON_TYPE)) {
			System.out.println("Apply Find");

			final StockFind stockFindMessage = message.as(STOCK_FIND_JSON_TYPE);

			Stockroom stockroom = context.storage().get(STOCKROOM).orElse(Stockroom.initEmpty());
			System.out.println("ItemID: " + stockFindMessage.getItemId() + ", Quantity: " + stockroom.getStockroom().get(stockFindMessage.getItemId()).quantity);

//			int itemId = stockFindMessage.getItemId();
//
//			int return_stock = -42;
//			if (itemId == 1) {
//				return_stock = context.storage().get(STOCK_COUNT).orElse(-1);
//			} else if (itemId == 2) {
//				return_stock = context.storage().get(STOCK_COUNT2).orElse(-2);
//			}
//
//			System.out.printf("FIND ITEM ID: %d, STOCK: %d%n", itemId, return_stock);

		} else if (message.is(STOCK_SUBTRACT_JSON_TYPE)) {
			System.out.println("Apply Subtract");

			final StockSubtract stockSubtractmessage = message.as(STOCK_SUBTRACT_JSON_TYPE);

			Stockroom stockroom = context.storage().get(STOCKROOM).orElse(Stockroom.initEmpty());
			stockroom.add(stockSubtractmessage.getItemId(), - stockSubtractmessage.getNumber()); // minus

			context.storage().set(STOCKROOM, stockroom);

		} else if (message.is(STOCK_ADD_JSON_TYPE)) {
			System.out.println("Apply Add");

			final StockAdd stockAddMessage = message.as(STOCK_ADD_JSON_TYPE);

//			int itemId = stockAddMessage.getItemId();
//			int itemNumber = stockAddMessage.getNumber();
//			System.out.println("ItemID: " + itemId + ", itemNumber: " + itemNumber);
//
//			if (itemId == 1) {
//				int stock_count = context.storage().get(STOCK_COUNT).orElse(0);
//				stock_count += itemNumber;
//				context.storage().set(STOCK_COUNT, stock_count);
//				System.out.println("ItemId " + itemId + " now has stock: " + stock_count);
//			} else if (itemId == 2) {
//				int stock_count = context.storage().get(STOCK_COUNT2).orElse(0);
//				stock_count += itemNumber;
//				context.storage().set(STOCK_COUNT2, stock_count);
//				System.out.println("ItemId " + itemId + " now has stock: " + stock_count);
//			}

			Stockroom stockroom = context.storage().get(STOCKROOM).orElse(Stockroom.initEmpty());
			stockroom.add(stockAddMessage.getItemId(), stockAddMessage.getNumber());

			context.storage().set(STOCKROOM, stockroom);

		} else if (message.is(STOCK_ITEM_CREATE_JSON_TYPE)) {
			System.out.println("Apply Item Create");

			final StockItemCreate stockItemCreateMessage = message.as(STOCK_ITEM_CREATE_JSON_TYPE);

			Stockroom stockroom = context.storage().get(STOCKROOM).orElse(Stockroom.initEmpty());
			stockroom.create(stockItemCreateMessage.getPrice());

			context.storage().set(STOCKROOM, stockroom);
		} else {
			throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
		}

		return context.done();
	}

	private static class Stockroom {

		private static final ObjectMapper mapper = new ObjectMapper();

		public static final Type<Stockroom> TYPE =
				SimpleType.simpleImmutableTypeFrom(
						TypeName.typeNameFromString("com.example/Stockroom"),
						mapper::writeValueAsBytes,
						bytes -> mapper.readValue(bytes, Stockroom.class));

		@JsonProperty("stockroom")
		private final ArrayList<Product> stockroom;

		public static Stockroom initEmpty() {
			return new Stockroom(new ArrayList<>());
		}

		@JsonCreator
		public Stockroom(@JsonProperty("stockroom") ArrayList<Product> stockroom) {
			this.stockroom = stockroom;
		}

		public int create(int price) {
			stockroom.add(new Product(price, 0));
			return stockroom.size() - 1;
		}

		public void add(int itemId, int quantity) {
			stockroom.get(itemId).addQuantity(quantity);
		}

		@JsonProperty("basket")
		public ArrayList<Product> getStockroom(){
			return stockroom;
		};

		public void clear() {
			stockroom.clear();
		}

		@Override
		public String toString() {
			return "Basket{" + "basket=" + stockroom + '}';
		}
	}

	private static class Product {
		private int price;
		private int quantity;

		public Product(int price, int quantity) {
			this.price = price;
			this.quantity = quantity;
		}

		public Product() {

		}

		public int getPrice() {
			return this.price;
		}

		public int getQuantity() {
			return this.quantity;
		}

		public void addQuantity(int n) {
			this.quantity += n;
		}
	}
}
