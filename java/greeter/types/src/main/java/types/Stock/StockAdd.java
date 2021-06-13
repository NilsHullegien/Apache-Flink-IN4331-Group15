package types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockAdd {

  @JsonProperty("number_add")
  private Integer numberAdd;

  public StockAdd() {}

  public StockAdd(Integer numberAdd) {
    this.numberAdd = numberAdd;
  }

  public Integer getNumberAdd() {
    return numberAdd;
  }
}
