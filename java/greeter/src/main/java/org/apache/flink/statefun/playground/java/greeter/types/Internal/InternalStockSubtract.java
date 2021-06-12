package org.apache.flink.statefun.playground.java.greeter.types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalStockSubtract {

  @JsonProperty("internal_subtract")
  private Integer value;

  public InternalStockSubtract() {}

  public InternalStockSubtract(Integer value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }
}
