package org.apache.flink.statefun.playground.java.greeter.types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockSubtract {
	@JsonProperty("item_id")
	private Integer itemId;

	@JsonProperty("number_subtract")
	private Integer number;

	public StockSubtract() {}

	public Integer getItemId() {
		return itemId;
	}

	public Integer getNumber() {
		return number;
	}
}
