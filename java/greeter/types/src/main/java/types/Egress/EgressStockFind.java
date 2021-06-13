package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressStockFind {

  @JsonProperty("amount")
  private Integer amount;

  @JsonProperty("price")
  private Integer price;

  public EgressStockFind() {}

  public EgressStockFind(Integer amount, Integer price) {
    this.amount = amount;
    this.price = price;
  }

  public Integer getPrice() {
    return price;
  }

  public Integer getAmount() {
    return amount;
  }
}
