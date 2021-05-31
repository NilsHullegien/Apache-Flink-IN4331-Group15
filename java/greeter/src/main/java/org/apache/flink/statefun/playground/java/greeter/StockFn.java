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

import static org.apache.flink.statefun.playground.java.greeter.types.Types.STOCK_FIND_PROTOBUF_TYPE;
import static org.apache.flink.statefun.playground.java.greeter.types.Types.USER_PROFILE_PROTOBUF_TYPE;

import java.util.concurrent.CompletableFuture;

import org.apache.flink.statefun.playground.java.greeter.types.generated.StockFind;
import org.apache.flink.statefun.playground.java.greeter.types.generated.UserProfile;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.io.KafkaEgressMessage;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.message.MessageBuilder;

/**
 * A simple function that computes personalized greetings messages based on a given {@link
 * UserProfile}. Then, it sends the greetings message back to the user via an egress Kafka topic.
 */
final class StockFn implements StatefulFunction {

	static final TypeName TYPENAME = TypeName.typeNameOf("stock.fns", "stock");
	static final StatefulFunctionSpec SPEC =
		StatefulFunctionSpec.builder(TYPENAME).withSupplier(StockFn::new).build();

	private static final ValueSpec<Integer> STOCK_COUNT = ValueSpec.named("stock_count").withIntType();
	private static final ValueSpec<Integer> STOCK_COUNT2 = ValueSpec.named("stock_count2").withIntType();

	private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("stock-namespace", "stock");

	@Override
	public CompletableFuture<Void> apply(Context context, Message message) {
		System.out.println("APPLY REACHED");
		if (message.is(STOCK_FIND_PROTOBUF_TYPE)) {
			final StockFind stockFindMessage = message.as(STOCK_FIND_PROTOBUF_TYPE);
			int itemId = stockFindMessage.getItemId();

			int return_stock = -42;
			if (itemId == 1) {
				return_stock = context.storage().get(STOCK_COUNT).orElse(-1);
			} else if (itemId == 2) {
				return_stock = context.storage().get(STOCK_COUNT2).orElse(-2);
			}

			System.out.printf("FIND ITEM ID: %d%n", itemId);

			context.send(
				KafkaEgressMessage.forEgress(KAFKA_EGRESS)
					.withTopic("stock_egress_topic")
					.withUtf8Key("STOCK")
					.withUtf8Value(String.valueOf(return_stock))
					.build());
		}
		return context.done();
	}
}
