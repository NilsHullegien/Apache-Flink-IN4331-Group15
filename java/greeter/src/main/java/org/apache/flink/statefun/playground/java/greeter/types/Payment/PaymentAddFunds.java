package org.apache.flink.statefun.playground.java.greeter.types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAddFunds {
	@JsonProperty("user_id")
	private Integer userId;

	@JsonProperty("amount")
	private Integer amount;

	public PaymentAddFunds() {
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getAmount() {
		return amount;
	}
}
