package types.Order;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderPaymentStatus {
    @JsonProperty("uId")
    private Integer uId;

    public OrderPaymentStatus() {
    }

    public OrderPaymentStatus(Integer uId) {
        this.uId = uId;
    }

    public Integer getUId() {
        return uId;
    }
}
