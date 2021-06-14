package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAddFunds {

  @JsonProperty("amount")
  private Integer amount;

  private Integer uId;

  public PaymentAddFunds() {}

  public PaymentAddFunds(Integer uId, Integer amount) {
      this.uId = uId;
      this.amount = amount;
  }

  public Integer getAmount() {
    return amount;
  }

  public Integer getOrderUId() {
      return uId;
  }
}
