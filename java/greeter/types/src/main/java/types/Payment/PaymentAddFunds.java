package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaymentAddFunds {

    @JsonProperty("amount")
    private Float amount;

    @JsonProperty("uid")
    private Integer uId;

    public PaymentAddFunds() {
    }

    public PaymentAddFunds(Integer uId, Float amount) {
        this.uId = uId;
        this.amount = amount;
    }

    public Float getAmount() {
        return amount;
    }

    public Integer getUId() {
        return uId;
    }
}
