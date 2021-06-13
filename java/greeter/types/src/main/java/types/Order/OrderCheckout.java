package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderCheckout {
  @JsonProperty("order_checkout_identifier")
  private Integer orderId;

  public OrderCheckout() {}

  public OrderCheckout(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
