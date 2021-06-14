package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentAddFundsResponse {

	@JsonProperty("done")
	private Boolean done;

	public PaymentAddFundsResponse() {}

	public PaymentAddFundsResponse(Boolean done) {
		this.done = done;
	}
}
