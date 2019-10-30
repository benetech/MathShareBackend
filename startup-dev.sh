#!/bin/bash

echo "ENV:"
printenv

./wait-for-it.sh postgresql:5432 -- supervisord --nodaemon --configuration /etc/supervisord.conf
