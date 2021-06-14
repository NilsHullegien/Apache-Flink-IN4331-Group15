package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentCreateUserResponse {
	@JsonProperty("user_id")
	private String userId;

	public PaymentCreateUserResponse() {}

	public PaymentCreateUserResponse(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

}
