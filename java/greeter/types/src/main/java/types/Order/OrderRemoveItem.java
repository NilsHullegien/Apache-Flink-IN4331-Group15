package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderRemoveItem {

  @JsonProperty("item_id_remove")
  private Integer itemId;

  public OrderRemoveItem() {}

  public Integer getItemId() {
    return itemId;
  }
}
