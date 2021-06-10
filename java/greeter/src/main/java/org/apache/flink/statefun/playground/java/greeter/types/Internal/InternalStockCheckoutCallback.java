package org.apache.flink.statefun.playground.java.greeter.types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalStockCheckoutCallback {
  @JsonProperty("ok")
  private Boolean ok;

  @JsonProperty("summed_cost")
  private Integer summed_cost;

  public InternalStockCheckoutCallback() {}

  public InternalStockCheckoutCallback(boolean ok, int summed_cost) {
    this.ok = ok;
    this.summed_cost = summed_cost;
  }

  public Boolean isOk() {
    return ok;
  }

  public Integer getSummed_cost() {
    return summed_cost;
  }


}
