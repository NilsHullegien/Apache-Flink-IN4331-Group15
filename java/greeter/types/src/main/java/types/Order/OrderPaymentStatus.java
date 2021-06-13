package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderPaymentStatus {
  @JsonProperty("payment_order_status")
  private Integer orderId;

  public OrderPaymentStatus() {}

  public Integer getOrderId() {
    return orderId;
  }
}
