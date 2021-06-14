package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressPaymentAddFunds {

  @JsonProperty("done")
  private Boolean done;

  public EgressPaymentAddFunds() {}

  public EgressPaymentAddFunds(Boolean done) {
    this.done = done;
  }

  public Boolean getDone() {
    return done;
  }
}
