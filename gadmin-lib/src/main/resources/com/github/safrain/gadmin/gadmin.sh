#! /bin/bash

DEFAULT_HOST="{{host}}"

host=$DEFAULT_HOST

path=$1

#Arg parsing
OPTIND=2
for (( i=2; i<=$#; i++ ))
do
        getopts ":h:" opt
        case $opt in
                h)host=$OPTARG;;
                :)echo "Please specify HOST.";;
                *)OPTIND=$(($OPTIND+1));;
        esac
done

if [ ! -z "$path" ];then
        #Post to server
        curl -s -X POST "$host" -T $path
else
        #Help screen
        echo ""
        echo " GAdmin bash client"
        echo ""
        echo " Usage: $0 <FILE> [-h HOST]"
        echo ""
        echo " * Default host('$DEFAULT_HOST') will be used if -h argument is not specified."
        echo " * To change default host settings, just edit $0 and modify the 'DEFAULT_HOST' variable."
        echo ""
fi