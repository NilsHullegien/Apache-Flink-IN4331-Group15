package org.apache.flink.statefun.playground.java.greeter.types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

/** NOT IMPLEMENTED */
public class PaymentCancel {
  @JsonProperty("user_id")
  private Integer userId;

  @JsonProperty("order_id")
  private Integer orderId;

  public PaymentCancel() {}

  public Integer getUserId() {
    return userId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
