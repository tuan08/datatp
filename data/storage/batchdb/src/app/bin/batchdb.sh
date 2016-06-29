#!/usr/bin/env bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

OS=`uname`

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

cd $bin

if [ "$OS" == "Linux" ] ; then
  JAVA_HOME="/opt/jdk1.6"
fi
JAVACMD="$JAVA_HOME/bin/java"

APP_HOME=`cd $bin/..; pwd; cd $bin`


LOG_FILE="$APP_HOME/logs/log.txt";
PID_FILE="$APP_HOME/logs/pid.txt";
if [ -d "$APP_HOME/logs" ] ; then
  echo "logs is existed!"
else
  mkdir $APP_HOME/logs
fi

LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"

#for f in $LIB/*.jar; do
#  CLASSPATH=${CLASSPATH}:$f;
#done

CLASSPATH="${CLASSPATH}:$LIB/*"
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

JAVA_LIBRARY_PATH="/opt/hadoop/cdh3/lib/native/Linux-amd64-64/"
HADOOP_OPTS="-Djava.library.path=$JAVA_LIBRARY_PATH"


###########################Start Profiling Config##########################################
JPDA_TRANSPORT=dt_socket
JPDA_ADDRESS=8000

REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

#for linux
LD_LIBRARY_PATH="/opt/Yourkit/bin/linux-x86-64/"
#for MAC
#DYLD_LIBRARY_PATH="/Users/tuannguyen/java/YourKit/bin/mac/"
#for Window

PATH="$PATH:$LD_LIBRARY_PATH"
export LD_LIBRARY_PATH DYLD_LIBRARY_PATH

#YOURKIT_PROFILE_OPTION="$REMOTE_DEBUG -agentlib:yjpagent  -Djava.awt.headless=false"
###########################Eng Profiling Config#############################################
COMMAND=$1
shift

MODE=$1
if [ "$MODE" = "-deamon" ] ; then
  shift ;
fi

#SYS_PROPS="-Danalysis.data.dir=$APP_HOME/data/analysis"
SYS_PROPS="$SYS_PROPS -Dapp.home.dir=$APP_HOME"
SYS_PROPS="$SYS_PROPS -Dhadoop.mapred.dir=/mnt/moom/hadoop/mapred"
#SYS_PROPS="$SYS_PROPS -Dhadoop.dfs.dir=/mnt/moom/hadoop/dfs"

# figure out which class to run
if [ "$COMMAND" = "export" ] ; then
  CLASS='net.datatp.storage.batchdb.document.Export'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"

  if [ "$MODE" = "-deamon" ] ; then
    nohup $JAVACMD $JAVA_OPTS $HADOOP_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $LOG_FILE 2>&1 < /dev/null &
    printf '%d' $! > $PID_FILE
  else
    exec $JAVACMD $JAVA_OPTS $HADOOP_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" 
  fi
elif [ "$COMMAND" = "import" ] ; then
  CLASS='net.datatp.storage.batchdb.document.Import'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx1024m"
  if [ "$MODE" = "-deamon" ] ; then
    nohup $JAVACMD $JAVA_OPTS $HADOOP_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $LOG_FILE 2>&1 < /dev/null &
    printf '%d' $! > $PID_FILE
  else
    exec $JAVACMD $JAVA_OPTS $HADOOP_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" 
  fi
elif [ "$COMMAND" = "dbstat" ] ; then
  CLASS='net.datatp.storage.batchdb.document.DocumentDBStat'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"
  if [ "$MODE" = "-deamon" ] ; then
    nohup $JAVACMD $JAVA_OPTS $HADOOP_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $LOG_FILE 2>&1 < /dev/null &
    printf '%d' $! > $PID_FILE
  else
    exec $JAVACMD $JAVA_OPTS $HADOOP_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" 
  fi
elif [ "$COMMAND" = "kill" ] ; then
  kill -9 `cat $PID_FILE` && rm -rf $PID_FILE 
fi
