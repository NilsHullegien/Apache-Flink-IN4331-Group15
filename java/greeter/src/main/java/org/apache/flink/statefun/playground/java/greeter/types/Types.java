package org.apache.flink.statefun.playground.java.greeter.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.statefun.playground.java.greeter.types.generated.UserProfile;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;

public final class Types {

  private Types() {}

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



}
