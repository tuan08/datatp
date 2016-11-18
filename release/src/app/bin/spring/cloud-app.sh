#!/bin/bash

if [ "x$JAVA_HOME" == "x" ] ; then 
  echo "WARNING JAVA_HOME is not set"
fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
APP_DIR=`cd $bin/../..; pwd; cd $bin`


JAVACMD=$JAVA_HOME/bin/java
JAVA_OPTS="-Xshare:auto -Xms128m -Xmx256m" 

LOG_FILE="$APP_DIR/logs/spring-test.log"
mkdir -p $APP_DIR/logs
PID_FILE="$bin/spring-test-pid.txt"



MAIN_CLASS="net.datatp.springframework.cloud.sample.Application"
RUN_CMD="\
  $JAVACMD \
  -Djava.ext.dirs=$APP_DIR/lib:$APP_DIR/lib/spring:$APP_DIR/lib/zookeeper \
  $JAVA_OPTS $MAIN_CLASS  \
  --logging.config=$APP_DIR/config/log4j2.yml"

exec $RUN_CMD

