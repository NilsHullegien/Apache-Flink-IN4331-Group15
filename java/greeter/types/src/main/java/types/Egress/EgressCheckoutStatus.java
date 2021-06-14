package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EgressCheckoutStatus {

    @JsonProperty
    private int uid;

    @JsonProperty("checkout_status")
    private Boolean checkout_status;

    public EgressCheckoutStatus() {}

    public EgressCheckoutStatus(int uid, Boolean checkout_status) {
        this.uid = uid;
        this.checkout_status = checkout_status;
    }

    public int getUid() {
        return uid;
    }

    public Boolean getCheckout_status() {
        return checkout_status;
    }

}
