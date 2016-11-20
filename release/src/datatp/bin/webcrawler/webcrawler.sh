#!/bin/bash

if [ "x$JAVA_HOME" == "x" ] ; then 
  echo "WARNING JAVA_HOME is not set"
fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
APP_DIR=`cd $bin/../..; pwd; cd $bin`


JAVACMD=$JAVA_HOME/bin/java
JAVA_OPTS="-Xshare:auto -Xms128m -Xmx256m" 

MAIN_CLASS="net.datatp.crawler.basic.CrawlerApp"
RUN_CMD="\
  $JAVACMD \
  -Djava.ext.dirs=$JAVA_HOME/jre/lib/ext:$APP_DIR/lib:$APP_DIR/lib/spring:$APP_DIR/lib/jetty:$APP_DIR/lib/esclient:$APP_DIR/lib/webcrawler \
  $JAVA_OPTS $MAIN_CLASS  \
  --logging.config=$APP_DIR/config/log4j2.yml \
  --server.port=8080 \
  --server.compression.enabled=true \
  --spring.cloud.zookeeper.enabled=false \
  --spring.http.multipart.enabled=true \
  --spring.http.multipart.location=build/upload \
  --crawler.site.config.file=$APP_DIR/config/webcrawler/site-config.json"

exec $RUN_CMD

