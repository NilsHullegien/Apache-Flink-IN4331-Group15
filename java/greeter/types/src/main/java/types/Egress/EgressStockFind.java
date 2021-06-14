package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressStockFind {

  @JsonProperty("stock")
  private Integer stock;

  @JsonProperty("price")
  private Integer price;

  public EgressStockFind() {}

  public EgressStockFind(Integer stock, Integer price) {
    this.stock = stock;
    this.price = price;
  }
}
