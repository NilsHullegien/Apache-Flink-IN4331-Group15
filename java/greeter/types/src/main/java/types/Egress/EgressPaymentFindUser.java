package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressPaymentFindUser {
	@JsonProperty("credit")
	private Float credit;

	public EgressPaymentFindUser() {
	}

	public EgressPaymentFindUser(Float credit) {
		this.credit = credit;
	}

}
