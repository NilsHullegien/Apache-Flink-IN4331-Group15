package types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockFind {
  @JsonProperty("stock_find_identifier")
  private Integer stockFindIdentifier;

  public StockFind() {}

  public StockFind(Integer stockFindIdentifier) {
    this.stockFindIdentifier = stockFindIdentifier;
  }

  public Integer getStockFindIdentifier() {
    return stockFindIdentifier;
  }
}
