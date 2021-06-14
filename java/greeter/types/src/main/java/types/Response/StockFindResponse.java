package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockFindResponse {
	@JsonProperty("stock")
	private Integer stock;

	@JsonProperty("price")
	private Integer price;

	public StockFindResponse() {}

	public StockFindResponse(Integer stock, Integer price) {
		this.stock = stock;
		this.price = price;
	}
}
