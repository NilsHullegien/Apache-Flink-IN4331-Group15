package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderPaymentStatus {
  @JsonProperty("payment_order_id")
  private Integer orderId;

  public OrderPaymentStatus() {}

  public OrderPaymentStatus(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
