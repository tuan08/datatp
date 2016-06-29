#!/bin/bash

cygwin=false
ismac=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
  Darwin) ismac=true;;
esac

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

export HADOOP_USER_NAME="neverwinterdp"

APP_DIR=`cd $bin/..; pwd; cd $bin`
JAVACMD=$JAVA_HOME/bin/java

if [ "x$JAVA_HOME" == "x" ] ; then 
  echo "WARNING JAVA_HOME is not set"
fi

(which $JAVACMD)
isjava=$?

if $ismac && [ $isjava -ne 0 ] ; then
  which java
  if [ $? -eq 0 ] ; then
    JAVACMD=`which java`
    echo "Defaulting to java: $JAVACMD"
  else 
    echo "JAVA Command (java) Not Found Exiting"
    exit 1
  fi
fi

FLINK_HOME="/opt/flink" 
JAVA_OPTS="-Xshare:auto -Xms128m -Xmx1536m -XX:-UseSplitVerifier" 
APP_OPT="-Dapp.dir=$APP_DIR -Duser.dir=$APP_DIR"

JAR_FILES="$APP_DIR/libs/module.flink-1.0-SNAPSHOT.jar"
JAR_FILES="$JAR_FILES,$APP_DIR/libs/module.kafka-0.8.2.0-1.0-SNAPSHOT.jar"
JAR_FILES="$JAR_FILES,$APP_DIR/libs/tools-1.0-SNAPSHOT.jar"
JAR_FILES="$JAR_FILES,$APP_DIR/libs/kafka-clients-0.8.2.0.jar"
JAR_FILES="$JAR_FILES,$APP_DIR/libs/kafka_2.10-0.8.2.0.jar"
JAR_FILES="$JAR_FILES,$APP_DIR/libs/zkclient-0.3.jar"
JAR_FILES="$JAR_FILES,$APP_DIR/libs/metrics-core-2.2.0.jar"

/opt/hadoop/bin/hdfs dfs -rm -R /tmp/perftest/*

echo "Launch the Flink Yarn Session"
export HADOOP_HOME=/opt/hadoop 
$FLINK_HOME/bin/yarn-session.sh -q 
$FLINK_HOME/bin/yarn-session.sh -d -jm 1024 -n 3 -tm 2048 -s 4

#sleep 15

echo "Run The PerfTest"
MAIN_CLASS="net.datatp.flink.perftest.PerfTest"
$JAVACMD -Djava.ext.dirs=$APP_DIR/libs:$FLINK_HOME/lib:$JAVA_HOME/jre/lib/ext $JAVA_OPTS $APP_OPT $LOG_OPT $MAIN_CLASS \
  --zk-connect zookeeper-1:2181\
  --kafka-connect kafka-1:9092,kafka-2:9092,kafka-3:9092 \
  --num-of-partition 2 \
  --num-of-message-per-partition 5000000 \
  --message-size 512 \
  --output-path hdfs://hadoop-master:9000/tmp/perftest \
  --flink-job-manager-host hadoop-worker-1 --flink-job-manager-port 32332 --flink-yarn-prop-file $FLINK_HOME/conf/.yarn-properties \
  --flink-parallelism 2 --flink-window-period-ms 1000 --flink-window-size 50000  --flink-jar-files $JAR_FILES
