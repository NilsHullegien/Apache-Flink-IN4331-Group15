package org.apache.flink.statefun.playground.java.greeter.types.Stock;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class StockSubtract {

	@JsonProperty("number_subtract")
	private Integer number;

	public StockSubtract() {};

	public StockSubtract(Integer number) {
		this.number = number;
	}

	public Integer getNumber() {
		return number;
	}
}
