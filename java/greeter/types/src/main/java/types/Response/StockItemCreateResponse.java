package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockItemCreateResponse {
	@JsonProperty("item_id")
	private Integer itemId;

	public StockItemCreateResponse() {}

	public StockItemCreateResponse(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getItemId() {
		return itemId;
	}
}
