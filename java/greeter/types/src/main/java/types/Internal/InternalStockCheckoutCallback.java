package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalStockCheckoutCallback {
  @JsonProperty("ok")
  private Boolean ok;

  @JsonProperty("summed_cost")
  private Float summedCost;

  public InternalStockCheckoutCallback() {}

  public InternalStockCheckoutCallback(boolean ok, Float summedCost) {
    this.ok = ok;
    this.summedCost = summedCost;
  }

  public Boolean isOk() {
    return ok;
  }

  public Float getSummedCost() {
    return summedCost;
  }
}
