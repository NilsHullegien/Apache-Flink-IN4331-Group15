package org.apache.flink.statefun.playground.java.types.org.apache.flink.statefun.playground.java.types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockItemCreate {
  @JsonProperty("price")
  private Integer price;

  public StockItemCreate() {}

  public StockItemCreate(Integer price) {
    System.out.println("STOCK ITEM CREATING");
    System.out.println(StockItemCreate.class.getCanonicalName());
    System.out.println(StockItemCreate.class.getName());
    this.price = price;
  }

  public Integer getPrice() {
    return price;
  }
}
