package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderDelete {
  @JsonProperty("order_delete_identifier")
  private Integer orderId;

  public OrderDelete() {}

  public OrderDelete(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
