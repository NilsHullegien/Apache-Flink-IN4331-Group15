package org.apache.flink.statefun.playground.java.greeter;

import static org.apache.flink.statefun.playground.java.greeter.types.Types.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;
import org.apache.flink.statefun.playground.java.greeter.types.Payment.PaymentAddFunds;
import org.apache.flink.statefun.sdk.java.*;
import org.apache.flink.statefun.sdk.java.message.Message;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

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
  public CompletableFuture<Void> apply(Context context, Message message) throws Exception {
    if (message.is(PAYMENT_ADD_FUNDS_JSON_TYPE)) {
      PaymentAddFunds addFundsMessage = message.as(PAYMENT_ADD_FUNDS_JSON_TYPE);
      User user = getUser(context);
      user.add(addFundsMessage.getAmount());
      context.storage().set(USER, user);
      System.out.println("Add user, new funds: " + user.funds);
    } else if (message.is(PAYMENT_STATUS_JSON_TYPE)) {
      //Internal message order isPaid
      //Return message true/false
    } else if (message.is(PAYMENT_CANCEL_JSON_TYPE)) {
      //Internal message get total cost for given order
      throw new Exception();
    } else if (message.is(PAYMENT_PAY_JSON_TYPE)) {
      //Pay order given by id (Internal to order => isPaid = true)
      throw new Exception();
    } else {
      throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
    }

    return context.done();
  }

  private User getUser(Context context) {
    User user = null;
    try {
      user =
          context.storage().get(USER).orElseThrow(() -> new Exception("User not initialized?"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return user;
  }


  private static class User {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static final Type<User> TYPE =
        SimpleType.simpleImmutableTypeFrom(
            TypeName.typeNameFromString("com.example/User"),
            mapper::writeValueAsBytes,
            bytes -> mapper.readValue(bytes, User.class));

    @JsonProperty("funds")
    private int funds;

    @JsonCreator
    public User() {
      this.funds = 0;
    }

    public void add(int addFunds) {
      this.funds += addFunds;
    }

    public void remove(int removeFunds) {
      this.funds -= removeFunds;
    }
  }
}
