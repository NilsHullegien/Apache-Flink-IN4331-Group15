package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderAddItem {

  @JsonProperty("item_id_add")
  private Integer itemId;

  public OrderAddItem() {}

  public Integer getItemId() {
    return itemId;
  }
}
