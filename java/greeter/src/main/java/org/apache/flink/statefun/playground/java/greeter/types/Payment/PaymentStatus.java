package org.apache.flink.statefun.playground.java.greeter.types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentStatus {
  @JsonProperty("payment_order_status")
  private Integer orderId;

  public PaymentStatus() {}

  public Integer getOrderId() {
    return orderId;
  }
}
