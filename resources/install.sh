#!/bin/bash

JAVA=java

cd `dirname $0`


CLASSPATH=verdandi.jar
for i in lib/*.jar; do
    CLASSPATH=$CLASSPATH:$i;
done

#-Dverdandi.confdir=verdandi2

$JAVA -cp $CLASSPATH verdandi.install.Installer
