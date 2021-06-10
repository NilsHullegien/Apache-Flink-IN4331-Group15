package org.apache.flink.statefun.playground.java.greeter.types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderCreate {
  @JsonProperty("user_id")
  private Integer userId;

  public OrderCreate() {}

  public Integer getUserId() {
    return userId;
  }
}
