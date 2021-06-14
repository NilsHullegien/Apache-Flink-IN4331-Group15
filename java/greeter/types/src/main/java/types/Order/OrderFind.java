package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderFind {

	@JsonProperty("uid")
	private Integer uId;

	@JsonProperty("order_Id")
	private Integer orderId;

	public OrderFind() {
	}

	public OrderFind(Integer uId, Integer orderId) {
		this.uId = uId;
		this.orderId = orderId;
	}

	public Integer getUId() {
		return uId;
	}

	public Integer getOrderId() {
		return orderId;
	}
}
