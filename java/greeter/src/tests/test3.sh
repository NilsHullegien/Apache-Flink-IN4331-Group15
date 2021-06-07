#!/bin/bash

source $(dirname "$0")/utils.sh

######## Scenario 3:
#  1) Create item of price 5, id = 0
#  2) Add 2 of item id 0
#  3) Create order, user id 0?
#  4) Order add item id 0
#  5) Order checkout -- should work
#  6) Order add item id 0 -- 2 in total now
#  7) Order checkout -- should fail
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
key="0"
json=$(cat <<JSON
 {"number_add":"2"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

key="0"
json=$(cat <<JSON
 {"stock_find_identifier":"0"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic

# 3)
key="0"
json=$(cat <<JSON
 {"user_id":"0"}
JSON
)
ingress_topic="order-create"

send_to_kafka $key $json $ingress_topic

# 4)
key="0"
json=$(cat <<JSON
 {"item_id_add":"0"}
JSON
)
ingress_topic="order-add-item"
send_to_kafka $key $json $ingress_topic

# 5)
key="0"
json=$(cat <<JSON
 {"order_checkout_identifier":"0"}
JSON
)
ingress_topic="order-checkout"
send_to_kafka $key $json $ingress_topic

key="0"
json=$(cat <<JSON
 {"stock_find_identifier":"0"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic

# 6)
key="0"
json=$(cat <<JSON
 {"item_id_add":"0"}
JSON
)
ingress_topic="order-add-item"
send_to_kafka $key $json $ingress_topic

# 7)
key="0"
json=$(cat <<JSON
 {"order_checkout_identifier":"0"}
JSON
)
ingress_topic="order-checkout"
send_to_kafka $key $json $ingress_topic

key="0"
json=$(cat <<JSON
 {"stock_find_identifier":"0"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic

