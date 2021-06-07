package org.apache.flink.statefun.playground.java.greeter.types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalStockSubtract {

    @JsonProperty("internal_subtract")
    private Integer number;

    public InternalStockSubtract() {}

    public InternalStockSubtract(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }
}