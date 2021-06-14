package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentCreateUser {
	@JsonProperty("user_Id")
	private Integer userId;

	public PaymentCreateUser() {
	}

	public PaymentCreateUser(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}
}
