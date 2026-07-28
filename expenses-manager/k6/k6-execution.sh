#!/bin/bash

if [ -z "$1" ]; then
    echo "Usage: ./run-k6.sh <filename>"
    exit 1
fi

K6_WEB_DASHBOARD=true K6_WEB_DASHBOARD_EXPORT=report.html k6 run "$1"