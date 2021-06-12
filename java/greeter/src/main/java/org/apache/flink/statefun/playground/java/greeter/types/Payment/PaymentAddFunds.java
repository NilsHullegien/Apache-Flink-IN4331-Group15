package org.apache.flink.statefun.playground.java.greeter.types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAddFunds {

  @JsonProperty("amount")
  private Integer amount;

  public PaymentAddFunds() {}

  public Integer getAmount() {
    return amount;
  }
}
