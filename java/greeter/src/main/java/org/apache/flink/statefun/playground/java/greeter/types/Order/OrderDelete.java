package org.apache.flink.statefun.playground.java.greeter.types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderDelete {
    @JsonProperty("order_delete_identifier")
    private Integer orderId;

    public OrderDelete() {
    }

    public Integer getOrderId() {
        return orderId;
    }
}
