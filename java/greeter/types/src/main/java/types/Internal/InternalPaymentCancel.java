package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalPaymentCancel {
  @JsonProperty("order_id")
  private Integer orderId;

  public InternalPaymentCancel() {}

  public InternalPaymentCancel(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
