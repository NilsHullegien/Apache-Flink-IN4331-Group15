package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderFind {
  @JsonProperty("order_find_identifier")
  private Integer orderId;

  public OrderFind() {}

  public OrderFind(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }
}
