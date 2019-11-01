#!/bin/bash

echo "ENV:"
printenv

supervisord --nodaemon --configuration /etc/supervisord.conf
