package org.apache.flink.statefun.playground.java.greeter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.playground.java.greeter.types.Payment.PaymentAddFunds;
import org.apache.flink.statefun.playground.java.greeter.types.Payment.PaymentCancel;
import org.apache.flink.statefun.playground.java.greeter.types.Payment.PaymentStatus;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.CompletableFuture;

import static org.apache.flink.statefun.playground.java.greeter.types.Types.*;

final class PaymentFn implements StatefulFunction {

	private static final ValueSpec<User> USER = ValueSpec.named("user").withCustomType(User.TYPE);

	static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "payment");
	static final StatefulFunctionSpec SPEC =
		StatefulFunctionSpec.builder(TYPENAME)
			.withValueSpecs(USER)
			.withSupplier(PaymentFn::new)
			.build();

	private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("order-namespace", "payment");

	@Override
	public CompletableFuture<Void> apply(Context context, Message message) {
		if (message.is(PAYMENT_ADD_FUNDS_JSON_TYPE)) {
			PaymentAddFunds addFundsMessage = message.as(PAYMENT_ADD_FUNDS_JSON_TYPE);


		} else if (message.is(PAYMENT_STATUS_JSON_TYPE)) {

		} else if (message.is(NYI_PAYMENT_CANCEL_JSON_TYPE)) {
			throw new NotImplementedException();
		} else if (message.is(NYI_PAYMENT_PAY_JSON_TYPE)) {
			throw new NotImplementedException();
		} else {
			throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
		}

		return context.done();
	}

	private static class User {

		private static final ObjectMapper mapper = new ObjectMapper();

		public static final Type<User> TYPE =
			SimpleType.simpleImmutableTypeFrom(
				TypeName.typeNameFromString("com.example/User"),
				mapper::writeValueAsBytes,
				bytes -> mapper.readValue(bytes, User.class));

	}
}
