package types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockItemCreate {
  @JsonProperty("price")
  private Integer price;

  public StockItemCreate() {}

  public StockItemCreate(Integer price) {
    this.price = price;
  }

  public Integer getPrice() {
    return price;
  }
}
