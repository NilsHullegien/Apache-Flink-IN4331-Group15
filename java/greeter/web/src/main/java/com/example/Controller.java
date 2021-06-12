package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.*;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.lang.*;
import java.io.*;
import java.util.*;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.util.backoff.FixedBackOff;

@RestController
public class Controller {

	private int order_id = 0;
	private int item_id = 0;
	volatile Hashtable<String, String> dict = new Hashtable<String, String>();
	private final Logger logger = LoggerFactory.getLogger(Application.class);

	private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

	@Autowired
	private KafkaTemplate<Object, Object> template;

	public void defferedReturn(DeferredResult<ResponseEntity<?>> output, String order_id) {
		while (dict.get(order_id).equals("")) {
			try {
				TimeUnit.SECONDS.sleep(1);
			}
			catch (Exception e){
				System.out.println("--------EXCEPTION----------");
			}
		}
		output.setResult(ResponseEntity.ok(dict.get(order_id)));
	}

	@KafkaListener(groupId = "OrderGroup", id = "OrderGroupReceive", topics = "create-order-receive")
	public void listenCreate(String order_id) {
		dict.put(order_id, order_id);
	}

	@KafkaListener(groupId = "OrderGroup", id = "OrderCheckoutReceive", topics = "checkout-receive")
	public void listen2Create(String order_id) {
		dict.put(order_id, order_id);
	}

	@KafkaListener(groupId = "OrderGroup", id = "OrderFindReceive", topics = "find-receive")
	public void listen3Create(String order_id) {
		dict.put(order_id, order_id);
	}

	//Get - creates an order for the given user, and returns an order_id
	@GetMapping(path = "/orders/create/{user_id}")
	public String createOrder(@PathVariable Integer user_id) {
		order_id++;
		this.template.send("order-create", order_id + "", "{\"user_id\":\"" + user_id + "\"}");

		dict.put(user_id + "", "");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, user_id + "");
		});

		return "{\"order_id\":" + order_id + "}";
	}

	//DELETE - deletes an order by ID
	@PostMapping(path = "/orders/remove/{order_id}")
	public void removeOrder(@PathVariable Integer order_id) {
		this.template.send("order-delete", order_id + "", "{\"order_delete_identifier\":\"" + order_id + "\"}");
	}

	//GET - retrieves the information of an order (id, payment status, items included and user id)
	@GetMapping(path = "/orders/find/{order_id}")
	public DeferredResult<ResponseEntity<?>> findOrder(@PathVariable Integer order_id) {
		this.template.send("order-find", order_id + "", "{\"order_find_identifier\":\"" + order_id + "\"}");

		dict.put(order_id + "", "");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, order_id + "");
		});

		return output;
	}

	//Post - adds a given item in the order given
	@PostMapping(path = "/orders/addItem/{order_id}/{item_id}")
	public void addOrder(@PathVariable Integer order_id, @PathVariable Integer item_id) {
		this.template.send("order-add-item", order_id + "", "{\"item_id_add\":\"" + item_id + "\"}");
	}

	//Post - remove a given item in the order given
	@PostMapping(path = "/orders/removeItem/{order_id}/{item_id}")
	public void removeItemOrder(@PathVariable Integer order_id, @PathVariable Integer item_id) {
		this.template.send("order-remove-item", order_id + "", "{\"item_id_remove\":\"" + item_id + "\"}");
	}

	//Get - remove a given item in the order given
	@GetMapping(path = "/orders/checkout/{order_id}")
	public DeferredResult<ResponseEntity<?>> checkoutOrder(@PathVariable Integer order_id) {
		this.template.send("order-checkout", order_id + "", "{\"order_checkout_identifier\":\"" + order_id + "\"}");

		dict.put(order_id + "", "");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, order_id + "");
		});

		return output;
	}

	//GET - retrieves the information of an item in stock
	@GetMapping(path = "/stock/find/{item_id}")
	public DeferredResult<ResponseEntity<?>> findStock(@PathVariable Integer item_id) {
		this.template.send("stock-find", item_id + "", "{\"stock_find_identifier\":\"" + item_id + "\"}");

		dict.put(item_id + "", "");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, item_id + "");
		});

		return output;
	}

	//GET - creates a item in the stock
	@GetMapping(path = "/stock/item/create/{price}")
	public DeferredResult<ResponseEntity<?>> createStock(@PathVariable Integer price) {
		item_id++;
		this.template.send("stock-item-create", item_id + "", "{\"price\":\"" + price + "\"}");

		dict.put(item_id + "", "");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, item_id + "");
		});

		return output;
	}

	//Post - add an item form the stock by the given amount
	@PostMapping(path = "/stock/add/{item_id}/{number_add}")
	public void addItemStock(@PathVariable Integer item_id, @PathVariable Integer number_add) {
		this.template.send("stock-add", item_id + "", "{\"number_add\":\"" + number_add + "\"}");
	}

	//Post - subtracts an item form the stock by the given amount
	@PostMapping(path = "/stock/subtract/{item_id}/{number_subtract}")
	public void subtractItemStock(@PathVariable Integer item_id, @PathVariable Integer number_subtract) {
		this.template.send("stock-subtract", item_id + "", "{\"number_subtract\":\"" + number_subtract + "\"}");
	}

	//Get - get payed status of an order
	@GetMapping(path = "/payment/status/{order_id}")
	public DeferredResult<ResponseEntity<?>> statusPayment(@PathVariable Integer order_id) {
		this.template.send("payment-status", order_id + "");

		dict.put(order_id + "", "");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, order_id + "");
		});

		return output;
	}

	//Get - add funds to user his account
	@GetMapping(path = "/payment/add_funds/{user_id}/{amount}")
	public DeferredResult<ResponseEntity<?>> addPayment(@PathVariable Integer user_id, @PathVariable Integer amount) {
		this.template.send("payment-add-funds", user_id + "", "{\"amount\":\"" + amount + "\"}");

		dict.put(user_id + "", "");
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, user_id + "");
		});

		return output;
	}
}
