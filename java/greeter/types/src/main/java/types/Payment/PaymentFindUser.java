package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentFindUser {
	@JsonProperty("user_Id")
	private Integer userId;

	@JsonProperty("uid")
	private Integer uId;

	public PaymentFindUser() {
	}

	public PaymentFindUser(Integer uId, Integer userId) {
		this.uId = uId;
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public Integer getUId() {
		return uId;
	}
}
