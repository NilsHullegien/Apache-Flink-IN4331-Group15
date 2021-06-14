package types.Order;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class OrderFind {

  @JsonProperty("uid")
  private Integer uId;

  public OrderFind() {}

  public OrderFind(Integer uId){
    this.uId = uId;
  }

  public Integer getUId() {
        return uId;
    }
}
