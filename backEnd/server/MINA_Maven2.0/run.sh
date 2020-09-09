#!/bin/bash

JAVA_HOME=/home/user02/bin/jdk
MAVEN_HOME=/home/user02/bin/maven
PATH=$MAVEN_HOME/bin:$JAVA_HOME/bin:$PATH

# $2是类型 $1是要查询的东西
# 3种类型分别是：
# 1. bw_id 返回转发关系
# 2. rpBody 返回博文内容
# 3. keyword 返回bw_id
mvn exec:java -Dexec.mainClass=NetClient.Client -Dexec.args="$1 $2"
