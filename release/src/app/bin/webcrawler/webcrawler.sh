#!/bin/bash

if [ "x$JAVA_HOME" == "x" ] ; then 
  echo "WARNING JAVA_HOME is not set"
fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
APP_DIR=`cd $bin/..; pwd; cd $bin`

export HADOOP_USER_NAME="dev"

SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

JAVACMD=$JAVA_HOME/bin/java
JAVA_OPTS="-Xshare:auto -Xms128m -Xmx512m -XX:-UseSplitVerifier" 

LOG_FILE="$APP_DIR/logs/tracking.stdout"
mkdir -p $APP_DIR/logs
PID_FILE="$bin/tracking-pid.txt"


function has_opt() {
  OPT_NAME=$1
  shift
  #Par the parameters
  for i in "$@"; do
    if [[ $i == $OPT_NAME ]] ; then
      echo "true"
      return
    fi
  done
  echo "false"
}

function trackingKafka() {
  MAIN_CLASS="net.datatp.tracking.kafka.KafkaTrackingApp"
  RUN_CMD="$JAVACMD -Djava.ext.dirs=$APP_DIR/libs:$JAVA_HOME/jre/lib/ext $JAVA_OPTS -Dtracking.config=$APP_DIR/conf/tracking-kafka.properties $MAIN_CLASS"

  DAEMON=$(has_opt "--daemon" $@ )
  if [ "$DAEMON" = "true" ] ; then
    exec $RUN_CMD
  else
    nohup $RUN_CMD > $LOG_FILE 2>&1 < /dev/null &
    printf '%d' $! > $PID_FILE
  fi
}

function trackingElasticsearch() {
  MAIN_CLASS="net.datatp.tracking.es.ESTrackingApp"
  RUN_CMD="$JAVACMD -Djava.ext.dirs=$APP_DIR/libs:$JAVA_HOME/jre/lib/ext $JAVA_OPTS -Dtracking.config=$APP_DIR/conf/tracking-es.properties $MAIN_CLASS"

  DAEMON=$(has_opt "--daemon" $@ )
  if [ "$DAEMON" = "true" ] ; then
    exec $RUN_CMD
  else
    nohup $RUN_CMD > $LOG_FILE 2>&1 < /dev/null &
    printf '%d' $! > $PID_FILE
  fi
}

COMMAND=$1
shift

if [ "$COMMAND" = "kafka" ] ; then
  trackingKafka
elif [ "$COMMAND" = "elasticsearch" ] ; then
  trackingElasticsearch
elif [ "$COMMAND" = "kill" ] ; then
  kill -9 `cat $PID_FILE` && rm -rf $PID_FILE
else
  echo "Usage:"
fi
