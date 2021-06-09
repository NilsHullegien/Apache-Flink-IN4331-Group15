/*
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.common.Order;
import com.common.Item;

@RestController
public class Controller {

	private int orderId = 0;
	private int itemId = 0;
	volatile Hashtable<String, String> dict = new Hashtable<String, String>();
	private final Logger logger = LoggerFactory.getLogger(Application.class);

	private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

	@Autowired
	private KafkaTemplate<String,String> template;

	@Autowired
	public Controller(KafkaTemplate<String, String> kafkaTemplate) {
		this.template = kafkaTemplate;
	}

	public void defferedReturn(DeferredResult<ResponseEntity<?>> output, String orderIdIndex) {
		while (dict.get(orderIdIndex) == "") {
			try {
				TimeUnit.SECONDS.sleep(1);
				logger.info("Sleeping");
			}
			catch (Exception e){
				logger.info("exception");
				System.out.println("--------EXEPTION----------");
			}
		}
		logger.info("output: " + dict.get(orderIdIndex));
		output.setResult(ResponseEntity.ok(dict.get(orderIdIndex)));
	}

	@KafkaListener(groupId = "OrderGroup", id = "OrderGroupReceive", topics = "createOrderReceive")
	public void listenCreate(String orderId2) {
		logger.info("Received change listener: " + orderId2);
		System.out.println("--------222222----------");
		dict.put(orderId2, orderId2);
	}

	@KafkaListener(groupId = "OrderGroup", id = "OrderCheckoutReceive", topics = "checkoutReceive")
	public void listen2Create(String orderId3) {
		System.out.println("--------3333333----------");
		logger.info("Received change listener: " + orderId3);
		dict.put(orderId3, orderId3);
	}

	@KafkaListener(groupId = "OrderGroup", id = "OrderFindReceive", topics = "findReceive")
	public void listen3Create(String orderId4) {
		System.out.println("--------44444----------");
		logger.info("Received change listener: " + orderId4);
		dict.put(orderId4, orderId4);
	}

	//Get - creates an order for the given user, and returns an order_id
	@GetMapping(path = "/orders/create/{userId}")
	public DeferredResult<ResponseEntity<?>> createOrder(@PathVariable String userId) {
		System.out.println("--------5555555----------");
		orderId++;
		dict.put(userId + "", "");
		this.template.send("order-create", new Order(userId, orderId).toString());
		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, userId + "");
		});

		return output;

		//return "{\"order_id\":"+orderId+"}";
	}

	//DELETE - deletes an order by ID
	@PostMapping(path = "/orders/remove/{orderId}")
	public void removeOrder(@PathVariable String orderId) {
		System.out.println("--------6666666----------");
		// this.template.send("createOrderReceive", orderId);
		this.template.send("checkoutReceive", orderId);
		// this.template.send("findReceive", orderId);
		//no return, just 200 HTTP code if succesfull
	}

	//GET - retrieves the information of an order (id, payment status, items included and user id)
	@GetMapping(path = "/orders/find/{order_id}")
	public DeferredResult<ResponseEntity<?>> findOrder(@PathVariable String order_id) {
		System.out.println("--------77777----------");
		this.template.send("findOrder", order_id);

		dict.put(order_id + "", "");

		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, order_id + "");
		});

		return output;
	}

	//Post - adds a given item in the order given
	@PostMapping(path = "/orders/addItem/{order_id}/{item_id}")
	public void addOrder(@PathVariable String order_id, @PathVariable String item_id) {
		System.out.println("--------8888888----------");
		String[] ids = new String[2];
		ids[0] = order_id;
		ids[1] = item_id;
		this.template.send("addItemOrder", Arrays.toString(ids));
	}

	//Post - remove a given item in the order given
	@PostMapping(path = "/orders/removeItem/{order_id}/{item_id}")
	public void removeItemOrder(@PathVariable String order_id, @PathVariable String item_id) {
		System.out.println("--------999999----------");
		String[] ids = new String[2];
		ids[0] = order_id;
		ids[1] = item_id;
		this.template.send("removeItemOrder", Arrays.toString(ids));
	}

	//Get - remove a given item in the order given
	@GetMapping(path = "/orders/checkout/{order_id}")
	public DeferredResult<ResponseEntity<?>> checkoutOrder(@PathVariable String order_id) {
		System.out.println("--------10-0000000----------");
		this.template.send("checkoutOrder", order_id);

		dict.put(order_id + "", "");

		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, order_id + "");
		});

		return output;
	}


	//GET - retrieves the information of an item in stock
	@GetMapping(path = "/stock/find/{item_id}")
	public DeferredResult<ResponseEntity<?>> findStock(@PathVariable String item_id) {
		System.out.println("--------11-0000000----------");
		this.template.send("findStock", item_id);

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
		System.out.println("--------12-0000000----------");
		itemId++;
		this.template.send("stock-item-create", "{\"price\":'" + price + "'}");

		dict.put(itemId + "", "");

		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, itemId + "");
		});

		return output;
	}

	//Post - add an item form the stock by the given amount
	@PostMapping(path = "/stock/add/{item_id}/{number}")
	public void addItemStock(@PathVariable String item_id, @PathVariable String number) {
		System.out.println("--------13-0000000----------");
		String[] ids = new String[2];
		ids[0] = item_id;
		ids[1] = number;
		this.template.send("addItemStock", Arrays.toString(ids));
	}

	//Post - subtracts an item form the stock by the given amount
	@PostMapping(path = "/stock/subtract/{item_id}/{number}")
	public void subtractItemStock(@PathVariable String item_id, @PathVariable String number) {
		System.out.println("--------14-0000000----------");
		String[] ids = new String[2];
		ids[0] = item_id;
		ids[1] = number;
		this.template.send("subtractItemStock", Arrays.toString(ids));
	}

	//Post - subtract amount of the order from the users credit
	@PostMapping(path = "/payment/pay/{user_id}/{order_id}/{amount}")
	public void payPayment(@PathVariable String user_id, @PathVariable String order_id, @PathVariable String amount) {
		System.out.println("--------15-0000000----------");
		String[] ids = new String[3];
		ids[0] = user_id;
		ids[1] = order_id;
		ids[2] = amount;
		this.template.send("payPayment", Arrays.toString(ids));
	}

	//Post - subtract amount of the order from the users credit
	@PostMapping(path = "/payment/cancel/{user_id}/{order_id}")
	public void cancelPayment(@PathVariable String user_id, @PathVariable String order_id) {
		System.out.println("--------16-0000000----------");
		String[] ids = new String[2];
		ids[0] = user_id;
		ids[1] = order_id;
		this.template.send("cancelPayment", Arrays.toString(ids));
	}

	//Get - get payed status of an order
	@GetMapping(path = "/payment/status/{order_id}")
	public DeferredResult<ResponseEntity<?>> statusPayment(@PathVariable String order_id) {
		System.out.println("--------17-0000000----------");
		this.template.send("statusPayment", order_id);

		dict.put(order_id + "", "");

		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, order_id + "");
		});

		return output;
	}

	//Get - add funds to user his account
	@GetMapping(path = "/payment/add_funds/{user_id}/{amount}")
	public DeferredResult<ResponseEntity<?>> addPayment(@PathVariable String user_id, @PathVariable String amount) {
		System.out.println("--------99 99 99 $----------");
		String[] ids = new String[2];
		ids[0] = user_id;
		ids[1] = amount;
		this.template.send("addPayment", Arrays.toString(ids));

		dict.put(user_id + "", "");

		DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

		ForkJoinPool.commonPool().submit(() -> {
			defferedReturn(output, user_id + "");
		});

		return output;
	}
}
