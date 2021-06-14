#!/bin/bash

source $(dirname "$0")/utils.sh

######## Scenario 3:
#  1) Create item of price 5, id = 0
#  2) Stock Add 2 of item id 0
#  3) Create order, user id 0?
#  4) Order add item id 0
#4.5) Find Order 0
#  5) Add funds 10
#  6) Order checkout -- should work
#  7) Order add item id 0 -- 2 in total now
#  8) Order checkout -- should fail  (not enough items)  (FAIL IF CLEAR CART IS NOT IMPLEMENTED)
#  9) Stock Add 1 of item id 0
# 10) Order checkout -- should fail (not enough funds)
# 11) Add 5 funds
# 12) Order checkout -- should work again
#--------------------------------
# 1)

key="0"
json=$(cat <<JSON
 {"price":"5"}
JSON
)
ingress_topic="stock-item-create" # StockFn
send_to_kafka $key $json $ingress_topic

# 2)
json=$(cat <<JSON
 {"number_add":"2"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 3)
json=$(cat <<JSON
 {"user_id":"0"}
JSON
)
ingress_topic="order-create" # StockFn
send_to_kafka $key $json $ingress_topic

# 4)
json=$(cat <<JSON
 {"item_id_add":"0"}
JSON
)
ingress_topic="order-add-item"
send_to_kafka $key $json $ingress_topic


json=$(cat <<JSON
 {"order_find_identifier": 0}
JSON
)
ingress_topic="order-find"
send_to_kafka $key $json $ingress_topic

# 5)
json=$(cat <<JSON
 {"amount":"10"}
JSON
)
ingress_topic="payment-add-funds"
send_to_kafka $key $json $ingress_topic

# 6)
json=$(cat <<JSON
 {"order_checkout_identifier":"0"}
JSON
)
ingress_topic="order-checkout"
send_to_kafka $key $json $ingress_topic

# 7)
json=$(cat <<JSON
 {"item_id_add":"0"}
JSON
)
ingress_topic="order-add-item"
send_to_kafka $key $json $ingress_topic

# 8)
json=$(cat <<JSON
 {"order_checkout_identifier":"0"}
JSON
)
ingress_topic="order-checkout"
send_to_kafka $key $json $ingress_topic

# 9)
json=$(cat <<JSON
 {"number_add":"1"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 10)
json=$(cat <<JSON
 {"order_checkout_identifier":"0"}
JSON
)
ingress_topic="order-checkout" # StockFn
send_to_kafka $key $json $ingress_topic

# 11)
json=$(cat <<JSON
 {"amount":"5"}
JSON
)
ingress_topic="payment-add-funds"
send_to_kafka $key $json $ingress_topic

# 12)
json=$(cat <<JSON
 {"order_checkout_identifier":"0"}
JSON
)
ingress_topic="order-checkout" # StockFn
send_to_kafka $key $json $ingress_topic
