#!/bin/bash

while [ -z "$(netstat -tln | grep 8081)" ]; do
    echo 'Waiting for Server to start ...'
    sleep 1
done
echo 'Server started.'

# Start server.
echo 'Starting Gateway...'

node /usr/src/app/build/server.js
