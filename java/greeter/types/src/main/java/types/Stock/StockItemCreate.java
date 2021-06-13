package types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockItemCreate {
  @JsonProperty("price")
  private Integer price;

  public StockItemCreate() {}

  public Integer getPrice() {
    return price;
  }
}
