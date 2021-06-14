package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderFind {
  @JsonProperty("order_find_identifier")
  private Integer orderId;

  private Integer uId;

  public OrderFind() {}

  public OrderFind(Integer uId, Integer orderId){
    this.uId = uId;
    this.orderId = orderId;
  }

  public Integer getOrderId() {
    return orderId;
  }

  public Integer getOrderUId() {
        return uId;
    }
}
