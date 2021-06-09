package org.apache.flink.statefun.playground.java.greeter.types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalStockCheckoutCallback {
  @JsonProperty("ok")
  private Boolean ok;

  public InternalStockCheckoutCallback() {}

  public InternalStockCheckoutCallback(boolean ok) {
    this.ok = ok;
  }

  public Boolean isOk() {
    return ok;
  }
}
