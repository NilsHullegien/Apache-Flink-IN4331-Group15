package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderCreateResponse {
	@JsonProperty("order_id")
	private Integer orderId;

	public OrderCreateResponse() {}

	public OrderCreateResponse(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderId() {
		return orderId;
	}
}
