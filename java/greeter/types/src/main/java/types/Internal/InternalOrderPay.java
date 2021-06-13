package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalOrderPay {
  @JsonProperty("paid")
  private Boolean paid;

  public InternalOrderPay() {}

  public InternalOrderPay(Boolean paid) {
    this.paid = paid;
  }

  public Boolean isPaid() {
    return paid;
  }
}
