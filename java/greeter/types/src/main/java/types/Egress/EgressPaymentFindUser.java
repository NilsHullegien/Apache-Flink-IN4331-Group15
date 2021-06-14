package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressPaymentFindUser {
	@JsonProperty("credit")
	private Integer credit;

	public EgressPaymentFindUser() {
	}

	public EgressPaymentFindUser(Integer credit) {
		this.credit = credit;
	}

}
