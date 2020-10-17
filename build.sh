#!/bin/bash
rm -Rf dist
mkdir -p dist
if [ "$GRADLE" = "" ];
then
    export GRADLE="`which gradle`"
fi
$GRADLE build
cp build/libs/SoftwareStore-*.jar dist/SoftwareStore.jar
cp -Rf www dist/


