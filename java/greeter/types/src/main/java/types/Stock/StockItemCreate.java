package types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockItemCreate {
  @JsonProperty("price")
  private Float price;

  public StockItemCreate() {}

  public StockItemCreate(Float price) {
    this.price = price;
  }

  public Float getPrice() {
    return price;
  }
}
