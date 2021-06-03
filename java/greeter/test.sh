#!/bin/bash

source $(dirname "$0")/utils.sh

######## Scenario 1:
#  1) Find ID 1 (-1)
#  2) Add 10 items to ID 1
#  3) Find ID 1 (should give more items)
#  4) Add 3 items to ID 1
#  5) Find ID 1 (more items)
#  6) Find ID 2 (should remain the same, -2 default)


#--------------------------------
# 1)

key="1"
json=$(cat <<JSON
 {"item_id":"1"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic

# 2)
key="2"
json=$(cat <<JSON
 {"item_id":"1","number_add":"10"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 3)
key="3"
json=$(cat <<JSON
 {"item_id":"1"}
JSON
)
ingress_topic="stock-find" # StockFn

send_to_kafka $key $json $ingress_topic

# 4)
key="4"
json=$(cat <<JSON
 {"item_id":"1","number_add":"3"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 5)
key="5"
json=$(cat <<JSON
 {"item_id":"1"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic

# 6)
key="6"
json=$(cat <<JSON
 {"item_id":"2"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic
######## Scenario 1:
#  1) add socks to stock (via StockFn)
#  2) put socks for userId "1" into the shopping cart (via UserShoppingCartFn)
#  3) checkout (via UserShoppingCartFn)
#--------------------------------
# 1)
#key="1" # itemId
#json=$(cat <<JSON
#  {"item_id":"2"}
#JSON
#)
#ingress_topic="stock_ingress_topic" # StockFn
#send_to_kafka $key $json $ingress_topic

