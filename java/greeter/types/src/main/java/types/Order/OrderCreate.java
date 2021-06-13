package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderCreate {
  @JsonProperty("user_id")
  private Integer userId;

  public OrderCreate() {}

  public OrderCreate(Integer userId) {
    this.userId = userId;
  }

  public Integer getUserId() {
    return userId;
  }
}
