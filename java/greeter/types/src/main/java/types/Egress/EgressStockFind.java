package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressStockFind {

  @JsonProperty("stock")
  private Integer stock;

  @JsonProperty("price")
  private Float price;

  public EgressStockFind() {}

  public EgressStockFind(Integer stock, Float price) {
    this.stock = stock;
    this.price = price;
  }
}
