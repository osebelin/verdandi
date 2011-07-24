#!/bin/bash


#JAVA_HOME=/home/java/versions/jdk/jdk1.6
#JAVA=$JAVA_HOME/bin/java

JAVA=java

cd `dirname $0`


CLASSPATH=$HOME/.verdandi/local:verdandi.jar
for i in lib/*.jar; do
    CLASSPATH=$CLASSPATH:$i;
done

$JAVA -cp $CLASSPATH verdandi.Main $@
