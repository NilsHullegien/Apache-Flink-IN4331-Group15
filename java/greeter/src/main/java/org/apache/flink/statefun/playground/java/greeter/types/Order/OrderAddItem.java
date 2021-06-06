package org.apache.flink.statefun.playground.java.greeter.types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderAddItem {
    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("item_id")
    private Integer itemId;

    public OrderAddItem() {
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getItemId() {
        return itemId;
    }
}
