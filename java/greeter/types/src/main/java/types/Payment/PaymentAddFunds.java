package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAddFunds {

  @JsonProperty("amount")
  private Integer amount;

  public PaymentAddFunds() {}

  public PaymentAddFunds(Integer amount) {
    this.amount = amount;
  }

  public Integer getAmount() {
    return amount;
  }
}
