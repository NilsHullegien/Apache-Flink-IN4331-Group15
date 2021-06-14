package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressPaymentStatus {

  @JsonProperty("paid")
  private Boolean paid;

  public EgressPaymentStatus() {}

  public EgressPaymentStatus(Boolean paid) {
    this.paid = paid;
  }

  public Boolean getPaid() {
    return paid;
  }
}
