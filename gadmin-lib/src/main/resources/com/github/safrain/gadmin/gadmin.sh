#! /bin/bash
HOST="{{host}}"

if [ ! -z "$1" ];then
        curl -s -X POST "${HOST}" -T $1
else
        echo ""
        echo "GAdmin bash client"
        echo ""
        echo "Usage: $0 <FILE>"
        echo ""
        echo "Server Url: ${HOST}"
        echo "To change default host settings, just edit $0 and modify the 'HOST' variable."
        echo ""
fi