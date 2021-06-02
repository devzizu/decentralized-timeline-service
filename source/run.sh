#!/bin/bash

BASE_PORT=10000

case $1 in
    "central")
        make run main="app.central.Central" args="-central central1"
        ;;
    
    "node")
        PUB=BASE_PORT+
        PULL=
        REPLY=
        make run main="app.node.Node" args="--register -node node1 -pub 10000 -pull 10001 -reply 10002"
        ;;
esac