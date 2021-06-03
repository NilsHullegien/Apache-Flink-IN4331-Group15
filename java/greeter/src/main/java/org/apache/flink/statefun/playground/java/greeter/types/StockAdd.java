package org.apache.flink.statefun.playground.java.greeter.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockAdd {
	@JsonProperty("item_id")
	private Integer itemId;

	@JsonProperty("number_add")
	private Integer number;

	public StockAdd() {}

	public Integer getItemId() {
		return itemId;
	}

	public Integer getNumber() {
		return number;
	}
}
