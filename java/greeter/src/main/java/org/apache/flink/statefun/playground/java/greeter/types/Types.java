package org.apache.flink.statefun.playground.java.greeter.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.playground.java.greeter.types.Order.*;
import org.apache.flink.statefun.playground.java.greeter.types.Stock.*;
import org.apache.flink.statefun.playground.java.greeter.types.Internal.*;
import org.apache.flink.statefun.playground.java.greeter.types.generated.UserProfile;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

public final class Types {

    private Types() {
    }

    private static final ObjectMapper JSON_OBJ_MAPPER = new ObjectMapper();
    private static final String TYPES_NAMESPACE = "greeter.types";

    public static final Type<UserLogin> USER_LOGIN_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, UserLogin.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, UserLogin.class));

    public static final Type<UserProfile> USER_PROFILE_PROTOBUF_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, UserProfile.getDescriptor().getFullName()),
                    UserProfile::toByteArray,
                    UserProfile::parseFrom);

    /**
     * STOCK
     */

    public static final Type<StockFind> STOCK_FIND_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, StockFind.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, StockFind.class));

    public static final Type<StockSubtract> STOCK_SUBTRACT_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, StockSubtract.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, StockSubtract.class));

    public static final Type<StockAdd> STOCK_ADD_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, StockAdd.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, StockAdd.class));

    public static final Type<StockItemCreate> STOCK_ITEM_CREATE_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, StockItemCreate.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, StockItemCreate.class));

    /**
     * ORDER
     */
    public static final Type<OrderAddItem> ORDER_ADD_ITEM_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, OrderAddItem.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, OrderAddItem.class));

    public static final Type<OrderCheckout> ORDER_CHECKOUT_JSON_TYPE =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, OrderCheckout.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, OrderCheckout.class));

  public static final Type<OrderCreate> ORDER_CREATE_JSON_TYPE =
          SimpleType.simpleImmutableTypeFrom(
                  TypeName.typeNameOf(TYPES_NAMESPACE, OrderCreate.class.getName()),
                  JSON_OBJ_MAPPER::writeValueAsBytes,
                  bytes -> JSON_OBJ_MAPPER.readValue(bytes, OrderCreate.class));

  public static final Type<OrderFind> ORDER_FIND_JSON_TYPE =
          SimpleType.simpleImmutableTypeFrom(
                  TypeName.typeNameOf(TYPES_NAMESPACE, OrderFind.class.getName()),
                  JSON_OBJ_MAPPER::writeValueAsBytes,
                  bytes -> JSON_OBJ_MAPPER.readValue(bytes, OrderFind.class));

  public static final Type<OrderDelete> ORDER_DELETE_JSON_TYPE =
          SimpleType.simpleImmutableTypeFrom(
                  TypeName.typeNameOf(TYPES_NAMESPACE, OrderDelete.class.getName()),
                  JSON_OBJ_MAPPER::writeValueAsBytes,
                  bytes -> JSON_OBJ_MAPPER.readValue(bytes, OrderDelete.class));

  public static final Type<OrderRemoveItem> ORDER_REMOVE_ITEM_JSON_TYPE =
          SimpleType.simpleImmutableTypeFrom(
                  TypeName.typeNameOf(TYPES_NAMESPACE, OrderRemoveItem.class.getName()),
                  JSON_OBJ_MAPPER::writeValueAsBytes,
                  bytes -> JSON_OBJ_MAPPER.readValue(bytes, OrderRemoveItem.class));


    /**
     * INTERNAL
     */

    /**
     * CHECKOUT
     */
    public static final Type<InternalStockCheckoutCallback> INTERNAL_STOCK_CHECKOUT_CALLBACK =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, InternalStockCheckoutCallback.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, InternalStockCheckoutCallback.class));

    public static final Type<InternalStockSubtract> INTERNAL_STOCK_SUBTRACT =
            SimpleType.simpleImmutableTypeFrom(
                    TypeName.typeNameOf(TYPES_NAMESPACE, InternalStockSubtract.class.getName()),
                    JSON_OBJ_MAPPER::writeValueAsBytes,
                    bytes -> JSON_OBJ_MAPPER.readValue(bytes, InternalStockSubtract.class));

}
