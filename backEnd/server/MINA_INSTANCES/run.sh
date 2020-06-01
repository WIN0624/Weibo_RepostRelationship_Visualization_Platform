#!/bin/bash

export JAVA_HOME=/usr/local/jdk1.8.0_151
export ANT_HOME=/usr/local/ant/apache-ant-1.10.1
export PATH=$JAVA_HOME/bin:$PATH:${ANT_HOME}/bin:$PATH
export CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH
export PATH=/usr/local:$PATH

export LC_NUMERIC="en_US.UTF-8"
export LANG=en_US.UTF-8

ant
cp=""
jars=(`ls ./lib/`)
for jar in ${jars[*]};
do
   cp=$cp":./lib/"$jar
done
jars=(`ls ./dest/`)
for jar in ${jars[*]};
do
   cp=$cp":./dest/"$jar
done

JVM_OPTS="$JVM_OPTS -XX:+UseParNewGC" 
JVM_OPTS="$JVM_OPTS -XX:+UseConcMarkSweepGC" 
JVM_OPTS="$JVM_OPTS -XX:+CMSParallelRemarkEnabled" 
JVM_OPTS="$JVM_OPTS -XX:SurvivorRatio=8" 
JVM_OPTS="$JVM_OPTS -XX:MaxTenuringThreshold=1"
JVM_OPTS="$JVM_OPTS -XX:CMSInitiatingOccupancyFraction=75"
JVM_OPTS="$JVM_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
JVM_OPTS="$JVM_OPTS -XX:+UseCMSCompactAtFullCollection"
JVM_OPTS="$JVM_OPTS -XX:CMSFullGCsBeforeCompaction=0"

JVM_GC_LOG="$JVM_GC_LOG -XX:+PrintGC"
JVM_GC_LOG="$JVM_GC_LOG -XX:+PrintGCDetails"
JVM_GC_LOG="$JVM_GC_LOG -XX:+PrintGCTimeStamps"
JVM_GC_LOG="$JVM_GC_LOG -XX:+PrintGCApplicationStoppedTime"
JVM_GC_LOG="$JVM_GC_LOG -XX:+PrintGCApplicationConcurrentTime"
JVM_GC_LOG="$JVM_GC_LOG -Xloggc:./jvm.gc.log"

#nohup java -Xmx2g -Xms2g -Xmn1g $JVM_OPTS $JVM_GC_LOG -cp $cp -Djava.library.path=../med-nlp/native/ NetClient.Client </dev/null &>nohup.out & sleep 1

java -Xmx2g -Xms2g -Xmn1g $JVM_OPTS $JVM_GC_LOG -cp $cp -Djava.library.path=../med-nlp/native/ NetClient.Client
