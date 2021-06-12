package org.apache.flink.statefun.playground.java.greeter.types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockSubtract {

  @JsonProperty("number_subtract")
  private Integer numberSubtract;

  public StockSubtract() {};

  public StockSubtract(Integer numberSubtract) {
    this.numberSubtract = numberSubtract;
  }

  public Integer getNumberSubtract() {
    return numberSubtract;
  }
}
