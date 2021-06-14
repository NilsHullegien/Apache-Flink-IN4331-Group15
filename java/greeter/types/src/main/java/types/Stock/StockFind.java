package types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockFind {

  @JsonProperty("uid")
  private Integer uId;

  public StockFind() {}

  public StockFind(Integer uId) {
    this.uId = uId;
  }

  public Integer getUId() {
      return uId;
  }

  @Override
  public String toString() {
    return "StockFind{" +
        "uId=" + uId +
        '}';
  }
}
