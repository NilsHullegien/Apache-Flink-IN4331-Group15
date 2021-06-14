package types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockFind {
  @JsonProperty("stock_find_identifier")
  private Integer stockFindIdentifier;

  private Integer uId;

  public StockFind() {}

  public StockFind(Integer uId, Integer stockFindIdentifier) {
    this.uId = uId;
    this.stockFindIdentifier = stockFindIdentifier;
  }

  public Integer getStockFindIdentifier() {
    return stockFindIdentifier;
  }

  public Integer getOrderUId() {
      return uId;
  }
}
