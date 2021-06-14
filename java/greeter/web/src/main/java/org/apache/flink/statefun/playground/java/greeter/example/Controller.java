package org.apache.flink.statefun.playground.java.greeter.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import types.Egress.EgressCheckoutStatus;
import types.Order.*;
import types.Payment.*;
import types.Response.*;
import types.Stock.*;

import java.util.Random;

import java.util.Hashtable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@RestController
public class Controller {

    private int order_id = 0;
    private int item_id = 0;
    private int user_id = 0;
    volatile Hashtable<Integer, Object> dict = new Hashtable<>();
    private final Random rand = new Random();

    @Autowired
    private KafkaTemplate<Object, Object> template;

    public DeferredResult<ResponseEntity<?>> deffer(Integer key) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            defferedReturn(output, key);
            System.out.println("RETURN DEFERRED FOR KEY " + key);
        });
        return output;
    }

    public void defferedReturn(DeferredResult<ResponseEntity<?>> output, Integer key) {
        while (!dict.containsKey(key)) {
            System.out.println("WAITING for key: " + key + " in dict " + dict);
            try {
                TimeUnit.MILLISECONDS.sleep(250);
            } catch (Exception e) {
                System.out.println("Deffered return exception");
            }
        }

        Object outputObject = dict.get(key);
        System.out.println("output obj = " + outputObject.getClass());
        if (outputObject instanceof EgressCheckoutStatus) {
            EgressCheckoutStatus outputEgressCheckoutStatus = (EgressCheckoutStatus) outputObject;
            if (outputEgressCheckoutStatus.getCheckout_status()) {
                output.setResult(ResponseEntity.ok(outputObject));
            } else {
                ResponseEntity retEntity = new ResponseEntity("failure", HttpStatus.BAD_REQUEST);
                output.setResult(retEntity);
            }
        } else {
            System.out.println("Didnt identify return obj 2");

            output.setResult(ResponseEntity.ok(outputObject));
        }
    }

    @KafkaListener(id = "egress-payment-add-funds", topics = "egress-payment-add-funds")
    public void listenPaymentAddFunds(ConsumerRecord<Object, Object> data) throws JsonProcessingException {
        dict.put(Integer.parseInt(data.key().toString()),
                new ObjectMapper().readValue(data.value().toString(), PaymentAddFundsResponse.class));
    }

    @KafkaListener(id = "egress-payment-status", topics = "egress-payment-status")
    public void listenPaymentStatus(ConsumerRecord<Object, Object> data) throws JsonProcessingException {
        dict.put(Integer.parseInt(data.key().toString()),
                new ObjectMapper().readValue(data.value().toString(), PaymentStatusResponse.class));
    }

    @KafkaListener(id = "egress-stock-find", topics = "egress-stock-find")
    public void listenStockFind(ConsumerRecord<Object, Object> data) throws JsonProcessingException {
        dict.put(Integer.parseInt(data.key().toString()),
                new ObjectMapper().readValue(data.value().toString(), StockFindResponse.class));
    }

    @KafkaListener(id = "egress-order-find", topics = "egress-order-find")
    public void listenOrderFind(ConsumerRecord<Object, Object> data) throws JsonProcessingException {
        dict.put(Integer.parseInt(data.key().toString()),
                new ObjectMapper().readValue(data.value().toString(), OrderFindResponse.class));
    }

    @KafkaListener(id = "egress-order-checkout", topics = "egress-order-checkout")
    public void listenOrderCheckout(ConsumerRecord<Object, Object> data) throws JsonProcessingException {
        System.out.println("RECEIVED MESSAGE");
        System.out.println(data);
        dict.put(Integer.parseInt(data.key().toString()), new ObjectMapper().readValue(data.value().toString(), EgressCheckoutStatus.class));
    }

    //POST - creates an order for the given user, and returns an order_id
    @PostMapping(path = "/orders/create/{user_id}")
    public ResponseEntity<?> createOrder(@PathVariable Integer user_id) {
        this.template.send("order-create", String.valueOf(++order_id), new OrderCreate(user_id));
        //deffer(String.valueOf(order_id)); TODO

        return ResponseEntity.ok(new OrderCreateResponse(order_id));
    }

    //DELETE - deletes an order by ID
    @PostMapping(path = "/orders/remove/{order_id}")
    public void removeOrder(@PathVariable Integer order_id) {
        this.template.send("order-delete", String.valueOf(order_id), new OrderDelete(order_id));
    }

    //GET - retrieves the information of an order (id, payment status, items included and user id)
    @GetMapping(path = "/orders/find/{order_id}")
    public DeferredResult<ResponseEntity<?>> findOrder(@PathVariable Integer order_id) {
        Integer uId = rand.nextInt();
        this.template.send("order-find", String.valueOf(order_id), new OrderFind(uId, order_id));
        return deffer(uId);
    }

    //Post - adds a given item in the order given
    @PostMapping(path = "/orders/addItem/{order_id}/{item_id}")
    public void addOrder(@PathVariable Integer order_id, @PathVariable Integer item_id) {
        this.template.send("order-add-item", String.valueOf(order_id), new OrderAddItem(item_id));
    }

    //Post - remove a given item in the order given
    @PostMapping(path = "/orders/removeItem/{order_id}/{item_id}")
    public void removeItemOrder(@PathVariable Integer order_id, @PathVariable Integer item_id) {
        this.template.send("order-remove-item", String.valueOf(order_id), new OrderRemoveItem(item_id));
    }

    //Get - remove a given item in the order given
    @PostMapping(path = "/orders/checkout/{order_id}")
    public DeferredResult<ResponseEntity<?>> checkoutOrder(@PathVariable Integer order_id) {
        Integer uId = rand.nextInt();
        this.template.send("order-checkout", String.valueOf(order_id), new OrderCheckout(uId, order_id));
        return deffer(uId);
    }

    //GET - retrieves the information of an item in stock
    @GetMapping(path = "/stock/find/{item_id}")
    public DeferredResult<ResponseEntity<?>> findStock(@PathVariable Integer item_id) {
        Integer uId = rand.nextInt();
        this.template.send("stock-find", String.valueOf(item_id), new StockFind(uId));
        return deffer(uId);
    }

    //POST - creates a item in the stock
    @PostMapping(path = "/stock/item/create/{price}")
    public ResponseEntity<?> createStock(@PathVariable Integer price) {
        this.template.send("stock-item-create", String.valueOf(++item_id), new StockItemCreate(price));
        return ResponseEntity.ok(new StockItemCreateResponse(item_id));
    }

    //Post - add an item form the stock by the given amount
    @PostMapping(path = "/stock/add/{item_id}/{number_add}")
    public void addItemStock(@PathVariable Integer item_id, @PathVariable Integer number_add) {
        this.template.send("stock-add", String.valueOf(item_id), new StockAdd(number_add));
    }

    //Post - subtracts an item form the stock by the given amount
    @PostMapping(path = "/stock/subtract/{item_id}/{number_subtract}")
    public void subtractItemStock(@PathVariable Integer item_id, @PathVariable Integer number_subtract) {
        this.template.send("stock-subtract", String.valueOf(item_id), new StockSubtract(number_subtract));
    }

    //Get - get payed status of an order
    @GetMapping(path = "/payment/status/{order_id}")
    public DeferredResult<ResponseEntity<?>> statusPayment(@PathVariable Integer order_id) {
        Integer uId = rand.nextInt();
        this.template.send("payment-status", String.valueOf(order_id), new OrderPaymentStatus(uId));
        return deffer(uId);
    }

    //Get - add funds to user his account
    @GetMapping(path = "/payment/add_funds/{user_id}/{amount}")
    public DeferredResult<ResponseEntity<?>> addPayment(@PathVariable Integer user_id, @PathVariable Integer amount) {
        Integer uId = rand.nextInt();
        this.template.send("payment-add-funds", String.valueOf(user_id), new PaymentAddFunds(uId, amount));
        return deffer(uId);
    }

    //POST - creates an new user, returns a user id
    @PostMapping(path = "/payment/create_user")
    public ResponseEntity<?> createUser() {
        this.template.send("payment-create_user", String.valueOf(++user_id), new PaymentCreateUser(user_id));
        return ResponseEntity.ok(new PaymentCreateUserResponse(user_id));
    }
}
