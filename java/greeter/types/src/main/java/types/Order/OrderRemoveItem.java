package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderRemoveItem {

  @JsonProperty("item_id_remove")
  private Integer itemId;

  public OrderRemoveItem() {}

  public OrderRemoveItem(Integer itemId) {
    this.itemId = itemId;
  }

  public Integer getItemId() {
    return itemId;
  }
}
