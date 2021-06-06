package org.apache.flink.statefun.playground.java.greeter.types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderFind {
    @JsonProperty("order_id")
    private Integer orderId;

    public OrderFind() {
    }

    public Integer getOrderId() {
        return orderId;
    }
}
