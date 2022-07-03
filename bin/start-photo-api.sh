#!/bin/bash
jarfile=/Users/iain/Code/Clojure/Luminus/photo-api/target/uberjar/photo-api.jar

echo "Starting photo api, look in /tmp/photo-api.log for messages."
java -Ddatabase-url="mongodb://127.0.0.1/photos" -jar $jarfile &>/tmp/photo-api.log &
