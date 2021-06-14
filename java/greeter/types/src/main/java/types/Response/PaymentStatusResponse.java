package types.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentStatusResponse {
	@JsonProperty("paid")
	private Boolean paid;

	public PaymentStatusResponse() {}

	public PaymentStatusResponse(Boolean paid) {
		this.paid = paid;
	}

	public Boolean getPaid() {
		return paid;
	}
}
