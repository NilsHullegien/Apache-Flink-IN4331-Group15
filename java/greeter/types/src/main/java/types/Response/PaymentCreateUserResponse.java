package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentCreateUserResponse {
	@JsonProperty("user_id")
	private Integer userId;

	public PaymentCreateUserResponse() {}

	public PaymentCreateUserResponse(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

}
