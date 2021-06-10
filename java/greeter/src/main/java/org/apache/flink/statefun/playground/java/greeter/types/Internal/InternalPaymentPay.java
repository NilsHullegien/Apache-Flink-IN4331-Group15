package org.apache.flink.statefun.playground.java.greeter.types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalPaymentPay {
  @JsonProperty("pay_amount")
  private Integer payAmount;

  public InternalPaymentPay() {}

  public InternalPaymentPay(int payAmount) {
    this.payAmount = payAmount;
  }


  public Integer getPayAmount() {
    return payAmount;
  }

}
