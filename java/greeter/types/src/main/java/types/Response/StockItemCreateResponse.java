package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockItemCreateResponse {
	@JsonProperty("item_id")
	private String itemId;

	public StockItemCreateResponse() {}

	public StockItemCreateResponse(String itemId) {
		this.itemId = itemId;
	}

	public String getItemId() {
		return itemId;
	}
}
