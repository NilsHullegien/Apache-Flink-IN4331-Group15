#!/bin/bash

source $(dirname "$0")/utils.sh

######## Scenario 1:
#  1) add socks to stock (via StockFn)
#  2) put socks for userId "1" into the shopping cart (via UserShoppingCartFn)
#  3) checkout (via UserShoppingCartFn)
#--------------------------------
# 1)
key="1" # itemId
#json=$(cat <<JSON
# {"user_id":"42069","user_name":"testUser","login_type":"WEB"}
#JSON
#)
json=$(cat <<JSON
 {"item_id":"42069"}
JSON
)
ingress_topic="user-logins" # StockFn
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

