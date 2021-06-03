#!/bin/bash

case $1 in
    "central")
        make run main="app.central.Central" args="-central central1"
        ;;
    
    "node")
        BASE=100
        BASE+=$(($2-1))
        BASE+="0"
        PUB=$(($BASE))
        PULL=$(($BASE+1))
        REPLY=$(($BASE+2))
        NAME="node"
        NAME+=$2
        make run main="app.node.Node" args="--$3 -node $NAME -pub $PUB -pull $PULL -reply $REPLY"
        ;;
esac