#!/bin/bash

source $(dirname "$0")/utils.sh

key="0"
json=$(cat <<JSON
 {"price":"5"}
JSON
)
ingress_topic="createOrderReceive" 
send_to_kafka $key $json $ingress_topic
