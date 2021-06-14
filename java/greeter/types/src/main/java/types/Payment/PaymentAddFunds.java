package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaymentAddFunds {

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("uid")
    private Integer uId;

    public PaymentAddFunds() {
    }

    public PaymentAddFunds(Integer uId, Integer amount) {
        this.uId = uId;
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getUId() {
        return uId;
    }
}
