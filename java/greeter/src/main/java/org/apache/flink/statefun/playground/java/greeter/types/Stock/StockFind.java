package org.apache.flink.statefun.playground.java.greeter.types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockFind {
  @JsonProperty("stock_find_identifier")
  private Integer itemId;

  public StockFind() {}

  public Integer getItemId() {
    return itemId;
  }
}
