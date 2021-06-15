package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalOrderFindCallback {
	@JsonProperty("summed_cost")
	private Float summed_cost;

	public InternalOrderFindCallback() {}

	public InternalOrderFindCallback(Float summed_cost) {
		this.summed_cost = summed_cost;
	}

	public Float getSummed_cost() {
		return summed_cost;
	}
}
