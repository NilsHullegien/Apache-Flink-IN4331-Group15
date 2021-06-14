package types.Egress;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class EgressOrderFind {

  @JsonProperty("order_id")
  private Integer order_id;

  @JsonProperty("paid")
  private Boolean paid;

  @JsonProperty("items")
  private HashMap<Integer, Integer> items;

  @JsonProperty("user_id")
  private Integer user_id;

  @JsonProperty("total_cost")
  private Integer total_cost;

  public EgressOrderFind() {}

  public EgressOrderFind(Integer order_id, Boolean paid, HashMap<Integer, Integer> items, Integer user_id, Integer total_cost) {
    this.order_id = order_id;
    this.paid = paid;
    this.items = items;
    this.user_id = user_id;
    this.total_cost = total_cost;
  }

  public Integer getOrder_id() {
    return order_id;
  }

  public Boolean getPaid() {
    return paid;
  }

  public HashMap<Integer, Integer> getItems() {
    return items;
  }

  public Integer getUser_id() {
    return user_id;
  }

  public Integer getTotal_cost() {
    return total_cost;
  }
}
