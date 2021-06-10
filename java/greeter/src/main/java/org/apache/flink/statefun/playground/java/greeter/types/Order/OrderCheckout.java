package org.apache.flink.statefun.playground.java.greeter.types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderCheckout {
  @JsonProperty("order_checkout_identifier")
  private Integer orderId;

  public OrderCheckout() {}

  public Integer getOrderId() {
    return orderId;
  }
}
