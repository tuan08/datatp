#!/usr/bin/bash

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

APP_HOME=`cd $bin/..; pwd; cd $bin`

if [ -d "$APP_HOME/logs" ] ; then
  echo "logs is existed!"
else
  mkdir $APP_HOME/logs
fi

LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"


CLASSPATH="${CLASSPATH}:$LIB/*"

if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi
####################Start Profiling Config###############################
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
###################Eng Profiling Config##################################

# get arguments
COMMAND=$1
shift

PID_FILE=$bin/pid.txt


JAVACMD="$JAVA_HOME/bin/java"

function doTrain() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx256m"
  CLASS='net.datatp.xhtml.text.classifier.ContentTypeTrainer'

  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@"
}

function doTest() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx256m"
  CLASS='net.datatp.xhtml.text.classifier.ContentTypeClassifier'

  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@"
}

function doReport() {
  echo "do report......................."
}

if [ "$COMMAND" = "train" ] ; then
  doTrain $@
elif [ "$COMMAND" = "test" ] ; then
  doTest $@
elif [ "$COMMAND" = "report" ] ; then
  doReport
else
  echo "content command options: "
  echo "  train     : To Train and generate the dictionary. "
  echo "  test      : To run the test"
  echo "  report    : To run the cross validation report"
fi
