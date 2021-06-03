package org.apache.flink.statefun.playground.java.greeter.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockFind {
	@JsonProperty("item_id")
	private Integer itemId;

	public StockFind() {}

	public Integer getItemId() {
		return itemId;
	}
}
