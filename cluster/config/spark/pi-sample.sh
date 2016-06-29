#!/bin/bash

HADOOP_CONF_DIR=/opt/hadoop/etc/hadoop ./bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --deploy-mode cluster --master yarn \
    --driver-memory 512m --executor-memory 512m --executor-cores 1 --queue default \
    lib/spark-examples*.jar  10
