#!/bin/bash
if [ "$RUNTIME" = "" ];
then
    export RUNTIME="`which podman`"
    if [ "$RUNTIME" = "" ];
    then
        export RUNTIME="`which docker`"
    fi
fi
$RUNTIME rmi jmestore
$RUNTIME build -t jmestore .