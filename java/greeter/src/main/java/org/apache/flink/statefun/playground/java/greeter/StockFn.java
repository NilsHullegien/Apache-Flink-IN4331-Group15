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

import java.util.concurrent.CompletableFuture;

import org.apache.flink.statefun.playground.java.greeter.types.StockAdd;
import org.apache.flink.statefun.playground.java.greeter.types.StockFind;
import org.apache.flink.statefun.playground.java.greeter.types.generated.UserProfile;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.io.KafkaEgressMessage;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;

import static org.apache.flink.statefun.playground.java.greeter.types.Types.*;

/**
 * A simple function that computes personalized greetings messages based on a given {@link
 * UserProfile}. Then, it sends the greetings message back to the user via an egress Kafka topic.
 */
final class StockFn implements StatefulFunction {

	private static final ValueSpec<Integer> STOCK_COUNT = ValueSpec.named("stock_count").withIntType();
	private static final ValueSpec<Integer> STOCK_COUNT2 = ValueSpec.named("stock_count2").withIntType();

	static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "stock");
	static final StatefulFunctionSpec SPEC =
		StatefulFunctionSpec.builder(TYPENAME)
			.withValueSpecs(STOCK_COUNT, STOCK_COUNT2)
			.withSupplier(StockFn::new)
			.build();

	private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("stock-namespace", "stock");

	@Override
	public CompletableFuture<Void> apply(Context context, Message message) {
		if (message.is(STOCK_FIND_JSON_TYPE)) {
			System.out.println("Apply Find");
			final StockFind stockFindMessage = message.as(STOCK_FIND_JSON_TYPE);
			int itemId = stockFindMessage.getItemId();

			int return_stock = -42;
			if (itemId == 1) {
				return_stock = context.storage().get(STOCK_COUNT).orElse(-1);
			} else if (itemId == 2) {
				return_stock = context.storage().get(STOCK_COUNT2).orElse(-2);
			}

			System.out.printf("FIND ITEM ID: %d, STOCK: %d%n", itemId, return_stock);

//			context.send(
//				KafkaEgressMessage.forEgress(KAFKA_EGRESS)
//					.withTopic("stock_egress_topic")
//					.withUtf8Key("STOCK")
//					.withUtf8Value(String.valueOf(return_stock))
//					.build());
		} else if (message.is(STOCK_SUBTRACT_JSON_TYPE)) {
			System.out.println("Apply Subtract");

		} else if (message.is(STOCK_ADD_JSON_TYPE)) {
			System.out.println("Apply Add");

			final StockAdd stockAddMessage = message.as(STOCK_ADD_JSON_TYPE);
			int itemId = stockAddMessage.getItemId();
			int itemNumber = stockAddMessage.getNumber();
			System.out.println("ItemID: " + itemId + ", itemNumber: " + itemNumber);

			if (itemId == 1) {
				int stock_count = context.storage().get(STOCK_COUNT).orElse(0);
				stock_count += itemNumber;
				context.storage().set(STOCK_COUNT, stock_count);
				System.out.println("ItemId " + itemId + " now has stock: " + stock_count);
			} else if (itemId == 2) {
				int stock_count = context.storage().get(STOCK_COUNT2).orElse(0);
				stock_count += itemNumber;
				context.storage().set(STOCK_COUNT2, stock_count);
				System.out.println("ItemId " + itemId + " now has stock: " + stock_count);
			}

		} else if (message.is(STOCK_ITEM_CREATE_JSON_TYPE)) {
			System.out.println("Apply Item Create");

		} else {
			throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
		}

		return context.done();
	}
}
