#!/bin/bash

if [ "x$JAVA_HOME" == "x" ] ; then 
  echo "WARNING JAVA_HOME is not set"
fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
APP_DIR=`cd $bin/../..; pwd; cd $bin`

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


JAVACMD=$JAVA_HOME/bin/java
JAVA_OPTS="-Xshare:auto -Xms128m -Xmx256m" 
LIB_DIR="$JAVA_HOME/jre/lib/ext:$APP_DIR/lib:$APP_DIR/lib/spring:$APP_DIR/lib/jetty"
LIB_DIR="$LIB_DIR:$APP_DIR/lib/esclient:$APP_DIR/lib/batchdb:$APP_DIR/lib/webcrawler"


MAIN_CLASS="net.datatp.crawler.BasicCrawlerApp"
#Optional
#--spring.mvc.view.prefix=/WEB-INF/jsp/ \
#--spring.mvc.view.suffix=.jsp \
RUN_CMD="\
  $JAVACMD \
  -Djava.ext.dirs=$LIB_DIR \
  $JAVA_OPTS $MAIN_CLASS  \
  --logging.config=$APP_DIR/config/log4j2.yml \
  --server.port=8080 \
  --server.compression.enabled=true \
  --server.error.whitelabel.enabled=false \
  --spring.cloud.zookeeper.enabled=false \
  --spring.http.multipart.enabled=true \
  --spring.http.multipart.location=build/upload \
  --crawler.url.recorddb.dir=$APP_DIR/data/urldb \
  --crawler.site.config.file=$APP_DIR/config/webcrawler/site-config.json"



DAEMON=$(has_opt "-d" $@ )
if [ "$DAEMON" = "true" ] ; then
  mkdir -p $APP_DIR/logs/webcrawler
  LOG_FILE="$APP_DIR/logs/webcrawler/webcrawler.stdout"
  PID_FILE="$APP_DIR/logs/webcrawler/pid.txt"
  cd $APP_DIR 
  nohup $RUN_CMD > $LOG_FILE 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
else
  cd $APP_DIR 
  exec $RUN_CMD
fi
