package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalStockPollValue {

	@JsonProperty("internal_item_count")
	private Integer count;

	public InternalStockPollValue() {}

	public InternalStockPollValue(Integer count) {
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}
}
