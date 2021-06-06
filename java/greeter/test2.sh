#!/bin/bash

source $(dirname "$0")/utils.sh

######## Scenario 2:
#  1) Create item

#--------------------------------
# 1)

key="1"
json=$(cat <<JSON
 {"price":"1"}
JSON
)
ingress_topic="stock-item-create" # StockFn
send_to_kafka $key $json $ingress_topic

# 2)
key="1"
json=$(cat <<JSON
 {"item_id":"0","number_add":"10"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 3)
key="1"
json=$(cat <<JSON
 {"item_id":"0"}
JSON
)
ingress_topic="stock-find" # StockFn

send_to_kafka $key $json $ingress_topic

# 4)
key="1"
json=$(cat <<JSON
 {"item_id":"0","number_add":"3"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 5)
key="1"
json=$(cat <<JSON
 {"item_id":"0"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic

# 6)
key="1"
json=$(cat <<JSON
 {"item_id":"0","number_subtract":"8"}
JSON
)
ingress_topic="stock-subtract" # StockFn
send_to_kafka $key $json $ingress_topic

# 7)
key="1"
json=$(cat <<JSON
 {"item_id":"0"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic





key="2"
json=$(cat <<JSON
 {"price":"20"}
JSON
)
ingress_topic="stock-item-create" # StockFn
send_to_kafka $key $json $ingress_topic

# 2)
key="2"
json=$(cat <<JSON
 {"item_id":"0","number_add":"20"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 3)
key="2"
json=$(cat <<JSON
 {"item_id":"0"}
JSON
)
ingress_topic="stock-find" # StockFn

send_to_kafka $key $json $ingress_topic

# 4)
key="2"
json=$(cat <<JSON
 {"item_id":"0","number_add":"1"}
JSON
)
ingress_topic="stock-add" # StockFn
send_to_kafka $key $json $ingress_topic

# 5)
key="2"
json=$(cat <<JSON
 {"item_id":"0"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic

# 6)
key="2"
json=$(cat <<JSON
 {"item_id":"0","number_subtract":"2"}
JSON
)
ingress_topic="stock-subtract" # StockFn
send_to_kafka $key $json $ingress_topic

# 7)
key="2"
json=$(cat <<JSON
 {"item_id":"0"}
JSON
)
ingress_topic="stock-find" # StockFn
send_to_kafka $key $json $ingress_topic