package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentStatus {
  @JsonProperty("payment_order_id")
  private Integer orderId;

  public PaymentStatus() {}

  public PaymentStatus(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
