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
import types.Egress.EgressOrderFind;
import types.Egress.EgressPaymentAddFunds;
import types.Egress.EgressPaymentFindUser;
import types.Internal.InternalOrderPay;
import types.Internal.InternalPaymentPay;
import types.Order.OrderFind;
import types.Payment.PaymentAddFunds;
import types.Payment.PaymentFindUser;

import static types.Types.*;

final class PaymentFn implements StatefulFunction {

    private static final ValueSpec<User> USER = ValueSpec.named("user").withCustomType(User.TYPE);

    static final TypeName TYPENAME = TypeName.typeNameOf("greeter.fns", "payment");
    static final StatefulFunctionSpec SPEC =
            StatefulFunctionSpec.builder(TYPENAME)
                    .withValueSpecs(USER)
                    .withSupplier(PaymentFn::new)
                    .build();

    private static final TypeName KAFKA_EGRESS = TypeName.typeNameOf("payment-namespace", "payment");

    @Override
    public CompletableFuture<Void> apply(Context context, Message message) throws Exception {
        if (message.is(PAYMENT_ADD_FUNDS_JSON_TYPE)) {
            System.out.println("APPLY PAYMENT ADD FUNDS");
            PaymentAddFunds addFundsMessage = message.as(PAYMENT_ADD_FUNDS_JSON_TYPE);
            User user = getUser(context);
            System.out.println("Funds before: " + user.funds);
            user.add(addFundsMessage.getAmount());
            context.storage().set(USER, user);
            System.out.println("Funds after: " + user.funds);

            EgressPaymentAddFunds egressMessage =
                    new EgressPaymentAddFunds(true);

            context.send(
                    KafkaEgressMessage.forEgress(KAFKA_EGRESS)
                            .withTopic("egress-payment-add-funds")
                            .withUtf8Key(String.valueOf(addFundsMessage.getUId()))
                            .withValue(EGRESS_PAYMENT_ADD_FUNDS, egressMessage)
                            .build());

        } else if (message.is(ORDER_PAYMENT_STATUS_JSON_TYPE)) {
            System.out.println("APPLY PAYMENT STATUS --- LOOP THROUGH ORDER, SHOULDNT SEE THIS MESSAGE");
            //      PaymentStatus paymentStatusMessage = message.as(PAYMENT_STATUS_JSON_TYPE);
            //
            //      final InternalOrderIsPaid internalOrderIsPaidMessage =
            //          new InternalOrderIsPaid(paymentStatusMessage.getOrderId());
            //      context.send(
            //          MessageBuilder.forAddress(OrderFn.TYPENAME,
            // paymentStatusMessage.getOrderId().toString())
            //              .withCustomType(INTERNAL_ORDER_IS_PAID, internalOrderIsPaidMessage)
            //              .build());

        } else if (message.is(INTERNAL_PAYMENT_PAY_JSON_TYPE)) {
            System.out.println("Apply Internal Payment Pay From Order");
            // Pay order given by id (Internal to order => isPaid = true)
            InternalPaymentPay paymentPayMessage = message.as(INTERNAL_PAYMENT_PAY_JSON_TYPE);
            User user = getUser(context);

            final InternalOrderPay internalOrderPayMessage;
            System.out.println(
                    "Checking with user funds: "
                            + user.funds
                            + " on pay amount: "
                            + paymentPayMessage.getPayAmount());
            if (user.funds >= paymentPayMessage.getPayAmount()) {
                System.out.println("User had funds: " + user.funds);
                user.remove(paymentPayMessage.getPayAmount());
                context.storage().set(USER, user);
                System.out.println("New user funds: " + user.funds);

                internalOrderPayMessage = new InternalOrderPay(true);
            } else {
                internalOrderPayMessage = new InternalOrderPay(false);
                System.out.println("Not enough funds to pay order");
            }

            Address caller;
            if (context.caller().isPresent()) {
                caller = context.caller().get();
            } else {
                throw new RuntimeException("CALLER NOT PRESENT");
            }

            context.send(
                    MessageBuilder.forAddress(caller)
                            .withCustomType(INTERNAL_ORDER_PAY, internalOrderPayMessage)
                            .build());

        } else if (message.is(PAYMENT_CREATE_USER_TYPE)) {
            context.storage().set(USER, context.storage().get(USER).orElse(new User()));

        } else if (message.is(PAYMENT_FIND_USER_TYPE)) {

            PaymentFindUser paymentFindUserMessage = message.as(PAYMENT_FIND_USER_TYPE);

            System.out.println("find user type");

            EgressPaymentFindUser egressMessage =
                    new EgressPaymentFindUser(getUser(context).funds);

            context.send(
                    KafkaEgressMessage.forEgress(KAFKA_EGRESS)
                            .withTopic("egress-payment-find_user")
                            .withUtf8Key(String.valueOf(paymentFindUserMessage.getUId()))
                            .withValue(EGRESS_PAYMENT_FIND_USER, egressMessage)
                            .build());

        } else {
            throw new IllegalArgumentException("Unexpected message type: " + message.valueTypeName());
        }

        return context.done();
    }

    private User getUser(Context context) {
        User user = null;
        try {
            user = context.storage().get(USER).orElse(new User());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    private static class User {

        private static final ObjectMapper mapper = new ObjectMapper();

        public static final Type<User> TYPE =
                SimpleType.simpleImmutableTypeFrom(
                        TypeName.typeNameFromString("org.apache.flink.statefun.playground.java.greeter.example/User"),
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
