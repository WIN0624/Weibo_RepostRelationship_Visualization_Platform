#!/bin/bash

JAVA_HOME=/home/user02/bin/jdk
MAVEN_HOME=/home/user02/bin/maven
PATH=$MAVEN_HOME/bin:$JAVA_HOME/bin:$PATH

mvn exec:java -Dexec.mainClass=NetClient.Client
