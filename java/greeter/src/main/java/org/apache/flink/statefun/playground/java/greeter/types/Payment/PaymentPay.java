package org.apache.flink.statefun.playground.java.greeter.types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * NOT IMPLEMENTED
 */
public class PaymentPay {
	@JsonProperty("user_id")
	private Integer userId;

	@JsonProperty("order_id")
	private Integer orderId;

	@JsonProperty("amount")
	private Integer amount;

	public PaymentPay() {
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getAmount() {
		return amount;
	}

	public Integer getOrderId() {
		return orderId;
	}
}
