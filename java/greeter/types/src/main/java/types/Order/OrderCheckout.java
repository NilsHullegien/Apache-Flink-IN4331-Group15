package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderCheckout {
  @JsonProperty("order_checkout_identifier")
  private Integer orderId;

  @JsonProperty("uid")
  private Integer uId;

  public OrderCheckout() {}

  public OrderCheckout(Integer uId, Integer orderId) {
    this.uId = uId;
    this.orderId = orderId;
  }

  public Integer getUId() {
    return uId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
