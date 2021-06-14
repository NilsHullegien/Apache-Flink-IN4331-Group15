package types;

import com.fasterxml.jackson.databind.ObjectMapper;
import types.Stock.StockItemCreate;
import org.apache.flink.statefun.sdk.java.TypeName;
import org.apache.flink.statefun.sdk.java.types.SimpleType;
import org.apache.flink.statefun.sdk.java.types.Type;
import types.Stock.*;
import types.Internal.*;
import types.Order.*;
import types.Payment.*;
import types.Egress.*;

public final class Types {

	private Types() {
	}

	private static final ObjectMapper JSON_OBJ_MAPPER = new ObjectMapper();
	private static final String TYPES_NAMESPACE = "types";

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

	public static final Type<OrderPaymentStatus> ORDER_PAYMENT_STATUS_JSON_TYPE =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, OrderPaymentStatus.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, OrderPaymentStatus.class));

	/**
	 * PAYMENT
	 */
	public static final Type<PaymentAddFunds> PAYMENT_ADD_FUNDS_JSON_TYPE =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, PaymentAddFunds.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, PaymentAddFunds.class));

	/**
	 * This one is actually an internal message
	 */
	public static final Type<InternalPaymentPay> INTERNAL_PAYMENT_PAY_JSON_TYPE =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, InternalPaymentPay.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, InternalPaymentPay.class));

	/**
	 * INTERNAL (CHECKOUT)
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

	public static final Type<InternalOrderIsPaid> INTERNAL_ORDER_IS_PAID =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, InternalOrderIsPaid.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, InternalOrderIsPaid.class));

	public static final Type<InternalPaymentCancel> INTERNAL_PAYMENT_CANCEL =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, InternalPaymentCancel.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, InternalPaymentCancel.class));

	public static final Type<InternalOrderPay> INTERNAL_ORDER_PAY =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, InternalOrderPay.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, InternalOrderPay.class));

	public static final Type<EgressStockFind> EGRESS_STOCK_FIND =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, EgressStockFind.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, EgressStockFind.class));

	public static final Type<EgressPaymentStatus> EGRESS_PAYMENT_STATUS =
		SimpleType.simpleImmutableTypeFrom(
			TypeName.typeNameOf(TYPES_NAMESPACE, EgressPaymentStatus.class.getName()),
			JSON_OBJ_MAPPER::writeValueAsBytes,
			bytes -> JSON_OBJ_MAPPER.readValue(bytes, EgressPaymentStatus.class));

	public static final Type<EgressOrderFind> EGRESS_ORDER_FIND =
			SimpleType.simpleImmutableTypeFrom(
					TypeName.typeNameOf(TYPES_NAMESPACE, EgressOrderFind.class.getName()),
					JSON_OBJ_MAPPER::writeValueAsBytes,
					bytes -> JSON_OBJ_MAPPER.readValue(bytes, EgressOrderFind.class));
}
