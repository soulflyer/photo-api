#!/bin/bash
echo "Starting photo api, look in /tmp/photo-api.log for messages."
java -Ddatabase-url="mongodb://127.0.0.1/photos" -jar /Users/iain/Code/Clojure/Luminus/photo-api/target/uberjar/photo-api.jar &>/tmp/photo-api.log &
