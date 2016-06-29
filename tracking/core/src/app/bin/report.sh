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

MAIN_CLASS="net.datatp.tracking.TrackingReportApp"
exec $JAVACMD -Djava.ext.dirs=$APP_DIR/libs:$JAVA_HOME/jre/lib/ext $JAVA_OPTS $MAIN_CLASS \
  --zk-connect zookeeper-1:2181 --tracking-path /tracking "$@"
