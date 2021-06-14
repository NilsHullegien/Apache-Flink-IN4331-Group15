package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalPaymentPay {
  @JsonProperty("pay_amount")
  private Float payAmount;

  public InternalPaymentPay() {}

  public InternalPaymentPay(Float payAmount) {
    this.payAmount = payAmount;
  }

  public Float getPayAmount() {
    return payAmount;
  }
}
