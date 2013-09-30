#! /bin/bash
curl -s "{{host}}?client" -o ./gadmin
echo "{{host}}" > ~/.gadmin_host
chmod +x ./gadmin
./gadmin