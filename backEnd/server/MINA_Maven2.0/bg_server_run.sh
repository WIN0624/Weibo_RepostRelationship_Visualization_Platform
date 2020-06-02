#!/bin/bash

JAVA_HOME=/home/user02/bin/jdk
MAVEN_HOME=/home/user02/bin/maven
PATH=$MAVEN_HOME/bin:$JAVA_HOME/bin:$PATH

mvn clean && mvn compile
if [ $? -eq 0 ]
then
    echo "Update successfully!"
    nohup mvn exec:java -Dexec.mainClass=NetServer.Server >> /home/user02/MINA_Weibo/weibo.log 2>&1 &
else
    echo "Eorro Happen! System exits!"
fi
