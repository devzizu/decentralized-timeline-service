#!/bin/bash

case $1 in
    "central")
        make run main="app.central.Central" args="-central central1"
        ;;
    
    "node")
        BASE=100
        BASE+=$(($2-1))
        BASE+="0"
        PUB=$(($BASE+1))
        PULL=$(($BASE+2))
        REPLY=$(($BASE+3))
        make run main="app.node.Node" args="--$3 -node node1 -pub $PUB -pull $PULL -reply $REPLY"
        ;;
esac