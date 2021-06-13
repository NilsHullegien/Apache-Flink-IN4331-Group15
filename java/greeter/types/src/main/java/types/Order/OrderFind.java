package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderFind {
  @JsonProperty("order_find_identifier")
  private Integer orderId;

  public OrderFind() {}

  public Integer getOrderId() {
    return orderId;
  }
}
