package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentFindUserResponse {
	@JsonProperty("credit")
	private Integer credit;

	public PaymentFindUserResponse() {}

	public PaymentFindUserResponse(Integer credit) {
		this.credit = credit;
	}

	public Integer getCredit() {
		return credit;
	}
}
