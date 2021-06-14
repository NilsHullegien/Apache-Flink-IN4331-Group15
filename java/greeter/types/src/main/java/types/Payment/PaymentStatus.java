package types.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class PaymentStatus {

    @JsonProperty("order_id")
    private Integer order_id;

    private Integer uId;

    public PaymentStatus() {
    }

    public PaymentStatus(Integer uId, Integer order_id) {
        this.uId = uId;
        this.order_id = order_id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public Integer getOrderUId() {
        return uId;
    }
}
