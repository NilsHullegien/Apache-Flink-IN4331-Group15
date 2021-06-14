package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentCreateUser {
	@JsonProperty("user_Id")
	private String userId;

	public PaymentCreateUser() {
	}

	public PaymentCreateUser(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}
}
