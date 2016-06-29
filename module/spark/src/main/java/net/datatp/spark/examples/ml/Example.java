package net.datatp.spark.examples.ml;

import org.apache.spark.sql.SparkSession;

abstract public class Example {
  abstract public void run(SparkSession spark) throws Exception ;
}
