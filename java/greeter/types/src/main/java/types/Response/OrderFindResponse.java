package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class OrderFindResponse {
	@JsonProperty("order_id")
	private Integer orderId;

	@JsonProperty("paid")
	private Boolean paid;

	@JsonProperty("items")
	private HashMap<Integer, Integer> items;

	@JsonProperty("user_id")
	private Integer userId;

	@JsonProperty("total_cost")
	private Integer totalCost;

	public OrderFindResponse() {}

	public OrderFindResponse(Integer orderId, Boolean paid, HashMap<Integer, Integer> items, Integer userId, Integer totalCost) {
		this.orderId = orderId;
		this.paid = paid;
		this.items = items;
		this.userId = userId;
		this.totalCost = totalCost;
	}
}
