#! /bin/bash

#Detect curl absence
hash curl 2>/dev/null || {
    echo -e "\nPlease intall 'curl' first.\n";
    exit 1;
}

#curl output pipe
tmp=`mktemp -d`
trap 'rm -rf "$tmpdir"' EXIT INT TERM HUP
pipe="${tmp}/pipe"
mkfifo $pipe

#Load last success host
if [ -f ~/.gadmin_host ];then
    last_host=`cat ~/.gadmin_host`
    host=$last_host
fi


#Detect arguments
while getopts ":h:lk:" opt; do
    case $opt in
        h)
            host=$OPTARG
            if [ "X${host}" != "X${last_host}" ];then
                echo $host > ~/.gadmin_host
            fi
            ;;
        l)
            action="gadmin.running"
            ;;
        k)
            action="gadmin.kill '$OPTARG'"
            ;;
        ##list process
        ##kill process
        ##
        \?)
            echo "Invalid option: -$OPTARG" >&2
            exit 1
            ;;
        :)
            echo "Option -$OPTARG requires an argument." >&2
            exit 1
            ;;
    esac
done
shift $((OPTIND-1))

#Detect null host
if [ -z $host ];then
     echo "Please specify host using -h switch."
fi

if [ -z "$action" ];then
    #Detect null argument
    if [ -z "$1" ];then
        echo ""
        echo "GAdmin bash client"
        echo ""
        echo "Usage: $0 <FILE>"
        echo ""
        echo "Server Url: ${HOST}"
        echo "To change host settings, just edit $0 and modify the 'HOST' variable."
        echo ""
        exit 1;
    fi

    #Detect file absence
    if [ ! -f $1 ];then
        echo "File '$1' does not exists."
        exit 1
    fi

    #Confirm
    echo -e "\E[44m"
    cat $1
    echo -e "\E[0m"
    echo
    echo -e "Contents above will be evalutated at \E[1;33m${host}\E[0m"
    echo
    echo -e -n "\E[1mAre you sure? [Y/N]:\E[0m"
    read -n 1
    echo

    if [[ ! $REPLY =~ ^[Yy]$ ]]
    then
        exit 1
    fi

    echo
    echo -e "\E[1;34mEvaluating '$1' on server...\E[0m"
    echo

    #Post file
    cat ${pipe} &
    http_code=`curl -s -N -X POST "${host}" -T $1 -o ${pipe} -w '%{http_code}'`
else
    #Post predefined command
    cat ${pipe} &
    http_code=`curl -s -X POST "${host}" -d "${action}" -o ${pipe} -w '%{http_code}'`
fi

RET=$?
if [ "X${RET}" != "X0" ];then
    echo -e "\E[1;31mError:\E[0m Error connecting to server."
    exit 1
fi
if [ "X${http_code}" != "X200" ];then
    echo -e "\E[1;33mWarning:\E[0m Server returned http status \E[1m${http_code}\E[0m."
else
    echo -e "\E[1;32mCompleted!\E[0m"
fi

