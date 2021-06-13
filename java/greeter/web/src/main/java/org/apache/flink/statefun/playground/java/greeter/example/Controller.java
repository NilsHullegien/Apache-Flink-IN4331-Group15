package org.apache.flink.statefun.playground.java.greeter.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.lang.*;
import java.util.*;

import org.apache.flink.statefun.playground.java.types.org.apache.flink.statefun.playground.java.types.Stock.StockItemCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.*;
import org.springframework.http.*;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.*;
import types.Stock.StockFind;

@RestController
public class Controller {

    private int order_id = 0;
    private int item_id = 0;
    volatile Hashtable<String, String> dict = new Hashtable<String, String>();

    private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

    @Autowired
    private KafkaTemplate<Object, Object> template;

    public DeferredResult<ResponseEntity<?>> deffer(String key) {
        dict.put(key, "");
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
        ForkJoinPool.commonPool().submit(() -> {
            defferedReturn(output, key);
        });
        return output;
    }

    public void defferedReturn(DeferredResult<ResponseEntity<?>> output, String order_id) {
        while (dict.get(order_id).equals("")) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                System.out.println("Deffered return exception");
            }
        }
        output.setResult(ResponseEntity.ok(dict.get(order_id)));
    }

    @KafkaListener(groupId = "group", id = "create-order-receive", topics = "create-order-receive")
    public void listen1Create(String order_id) {
        dict.put(order_id, order_id);
    }

    @KafkaListener(groupId = "group", id = "checkout-receive", topics = "checkout-receive")
    public void listen2Create(String order_id) {
        dict.put(order_id, order_id);
    }

    @KafkaListener(groupId = "group", id = "find-receive", topics = "find-receive")
    public void listen3Create(String order_id) {
        dict.put(order_id, order_id);
    }

    //Get - creates an order for the given user, and returns an order_id
    @GetMapping(path = "/orders/create/{user_id}")
    public String createOrder(@PathVariable Integer user_id) {
        order_id++;
        StockFind message = new StockFind(user_id);

        ListenableFuture<SendResult<Object, Object>> future = this.template.send("order-create", String.valueOf(order_id), message);
        future.addCallback(new KafkaSendCallback<Object, Object>() {
            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                System.out.println("GREAT SUCCESS");
            }

            @Override
            public void onFailure(KafkaProducerException ex) {
                System.out.println("FAILURE");
                ex.printStackTrace();
            }
        });
//        deffer(String.valueOf(order_id));
        System.out.println("SUCCEEDED");
        return "{\"order_id\":" + order_id + "}";
    }

    //DELETE - deletes an order by ID
    @PostMapping(path = "/orders/remove/{order_id}")
    public void removeOrder(@PathVariable Integer order_id) {
        this.template.send("order-delete", String.valueOf(order_id), "{\"order_delete_identifier\":\"" + order_id + "\"}");
    }

    //GET - retrieves the information of an order (id, payment status, items included and user id)
    @GetMapping(path = "/orders/find/{order_id}")
    public DeferredResult<ResponseEntity<?>> findOrder(@PathVariable Integer order_id) {
        this.template.send("order-find", String.valueOf(order_id), "{\"order_find_identifier\":\"" + order_id + "\"}");
        return deffer(String.valueOf(order_id));
    }

    //Post - adds a given item in the order given
    @PostMapping(path = "/orders/addItem/{order_id}/{item_id}")
    public void addOrder(@PathVariable Integer order_id, @PathVariable Integer item_id) {
        this.template.send("order-add-item", String.valueOf(order_id), "{\"item_id_add\":\"" + item_id + "\"}");
    }

    //Post - remove a given item in the order given
    @PostMapping(path = "/orders/removeItem/{order_id}/{item_id}")
    public void removeItemOrder(@PathVariable Integer order_id, @PathVariable Integer item_id) {
        this.template.send("order-remove-item", String.valueOf(order_id), "{\"item_id_remove\":\"" + item_id + "\"}");
    }

    //Get - remove a given item in the order given
    @GetMapping(path = "/orders/checkout/{order_id}")
    public DeferredResult<ResponseEntity<?>> checkoutOrder(@PathVariable Integer order_id) {
        this.template.send("order-checkout", String.valueOf(order_id), "{\"order_checkout_identifier\":\"" + order_id + "\"}");
        return deffer(String.valueOf(order_id));
    }

    //GET - retrieves the information of an item in stock
    @GetMapping(path = "/stock/find/{item_id}")
    public DeferredResult<ResponseEntity<?>> findStock(@PathVariable Integer item_id) {
        this.template.send("stock-find", String.valueOf(item_id), "{\"stock_find_identifier\":\"" + item_id + "\"}");
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
        this.template.send("stock-add", String.valueOf(item_id), "{\"number_add\":\"" + number_add + "\"}");
    }

    //Post - subtracts an item form the stock by the given amount
    @PostMapping(path = "/stock/subtract/{item_id}/{number_subtract}")
    public void subtractItemStock(@PathVariable Integer item_id, @PathVariable Integer number_subtract) {
        this.template.send("stock-subtract", String.valueOf(item_id), "{\"number_subtract\":\"" + number_subtract + "\"}");
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
        this.template.send("payment-add-funds", String.valueOf(user_id), "{\"amount\":\"" + amount + "\"}");
        return deffer(String.valueOf(user_id));
    }
}
