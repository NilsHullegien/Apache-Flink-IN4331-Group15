package org.apache.flink.statefun.playground.java.greeter.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import types.Stock.*;
import types.Payment.*;
import types.Order.*;

import java.util.Hashtable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@RestController
public class Controller {

    private int order_id = 0;
    private int item_id = 0;
    volatile Hashtable<String, String> dict = new Hashtable<String, String>();

    @Autowired
    private KafkaTemplate<Object, Object> template;

    public DeferredResult<ResponseEntity<?>> deffer(String key) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            defferedReturn(output, key);
        });
        return output;
    }

    public void defferedReturn(DeferredResult<ResponseEntity<?>> output, String key) {
        while (!dict.containsKey(key)) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                System.out.println("Deffered return exception");
            }
        }
        String outputString = dict.get(key);
        dict.remove(key);
        output.setResult(ResponseEntity.ok(outputString));
    }

    @KafkaListener(groupId = "group", id = "create-order-receive", topics = "create-order-receive")
    public void listen1Create(@Payload String message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key) {
        dict.put(String.valueOf(key), message);
    }

    @KafkaListener(groupId = "group", id = "checkout-receive", topics = "checkout-receive")
    public void listen2Create(@Payload String message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key) {
        dict.put(String.valueOf(key), message);
    }

    @KafkaListener(groupId = "group", id = "find-receive", topics = "find-receive")
    public void listen3Create(@Payload String message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key) {
        dict.put(String.valueOf(key), message);
    }

    @KafkaListener(groupId = "group", id = "egress-stock-find", topics = "egress-stock-find")
    public void listen4Create(@Payload String message, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key) {
        System.out.println("MESSAGE BACK HOLY FUCK WE KUNNEN EEN SOORT VAN IETS");
        dict.put(String.valueOf(key), message);
    }

    //Get - creates an order for the given user, and returns an order_id
    @GetMapping(path = "/orders/create/{user_id}")
    public String createOrder(@PathVariable Integer user_id) {
        order_id++;
        OrderCreate message = new OrderCreate(user_id);

        this.template.send("order-create", String.valueOf(order_id), message);
        deffer(String.valueOf(order_id));

        return "{\"order_id\":" + order_id + "}";
    }

    //DELETE - deletes an order by ID
    @PostMapping(path = "/orders/remove/{order_id}")
    public void removeOrder(@PathVariable Integer order_id) {
        this.template.send("order-delete", String.valueOf(order_id), new OrderDelete(order_id));
    }

    //GET - retrieves the information of an order (id, payment status, items included and user id)
    @GetMapping(path = "/orders/find/{order_id}")
    public DeferredResult<ResponseEntity<?>> findOrder(@PathVariable Integer order_id) {
        this.template.send("order-find", String.valueOf(order_id), new OrderFind(order_id));
        return deffer(String.valueOf(order_id));
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
    @GetMapping(path = "/orders/checkout/{order_id}")
    public DeferredResult<ResponseEntity<?>> checkoutOrder(@PathVariable Integer order_id) {
        this.template.send("order-checkout", String.valueOf(order_id), new OrderCheckout(order_id));
        return deffer(String.valueOf(order_id));
    }

    //GET - retrieves the information of an item in stock
    @GetMapping(path = "/stock/find/{item_id}")
    public DeferredResult<ResponseEntity<?>> findStock(@PathVariable Integer item_id) {
        this.template.send("stock-find", String.valueOf(item_id), new StockFind(item_id));
        return deffer(String.valueOf(item_id));
    }

    //GET - creates a item in the stock
    @GetMapping(path = "/stock/item/create/{price}")
    public DeferredResult<ResponseEntity<?>> createStock(@PathVariable Integer price) {
        StockItemCreate message = new StockItemCreate(price);
        item_id++;
        this.template.send("stock-item-create", String.valueOf(item_id), message);
        return deffer(String.valueOf(item_id));
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
        this.template.send("payment-status", String.valueOf(order_id));
        return deffer(String.valueOf(order_id));
    }

    //Get - add funds to user his account
    @GetMapping(path = "/payment/add_funds/{user_id}/{amount}")
    public DeferredResult<ResponseEntity<?>> addPayment(@PathVariable Integer user_id, @PathVariable Integer amount) {
        this.template.send("payment-add-funds", String.valueOf(user_id), new PaymentAddFunds(amount));
        return deffer(String.valueOf(user_id));
    }
}
