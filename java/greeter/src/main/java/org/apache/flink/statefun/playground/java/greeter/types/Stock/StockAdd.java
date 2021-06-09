package org.apache.flink.statefun.playground.java.greeter.types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockAdd {

  @JsonProperty("number_add")
  private Integer number;

  public StockAdd() {}

  public StockAdd(Integer number) {
    this.number = number;
  }

  public Integer getNumber() {
    return number;
  }
}
