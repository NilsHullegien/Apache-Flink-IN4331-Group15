package types.Internal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalOrderFindCallback {
	@JsonProperty("summed_cost")
	private Integer summed_cost;

	public InternalOrderFindCallback() {}

	public InternalOrderFindCallback(int summed_cost) {
		this.summed_cost = summed_cost;
	}

	public Integer getSummed_cost() {
		return summed_cost;
	}
}
