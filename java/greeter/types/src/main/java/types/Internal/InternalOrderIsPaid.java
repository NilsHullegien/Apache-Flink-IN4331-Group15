package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalOrderIsPaid {
  @JsonProperty("order_id")
  private Integer orderId;

  public InternalOrderIsPaid() {}

  public InternalOrderIsPaid(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
