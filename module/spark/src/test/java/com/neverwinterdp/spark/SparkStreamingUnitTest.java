package com.neverwinterdp.spark;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.junit.Test;

import scala.Tuple2;

public class SparkStreamingUnitTest implements Serializable {
  
  @Test
  public void streaming() throws Exception {
    //Create a local StreamingContext with two working thread and batch interval of 1 second
    SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
    JavaStreamingContext spark = new JavaStreamingContext(conf, Durations.seconds(1));
    
    //Create a DStream that will connect to hostname:port, like localhost:9999
    JavaReceiverInputDStream<String> lines = spark.socketTextStream("localhost", 9999);

    //Split each line into words
    JavaDStream<String> words = lines.flatMap(
      new FlatMapFunction<String, String>() {
        @Override 
        public Iterator<String> call(String x) {
          return Arrays.asList(x.split(" ")).listIterator();
        }
      }
    );

    //Count each word in each batch
    JavaPairDStream<String, Integer> pairs = words.mapToPair(
      new PairFunction<String, String, Integer>() {
        @Override public Tuple2<String, Integer> call(String s) {
          return new Tuple2<String, Integer>(s, 1);
        }
      }
    );
    JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
      new Function2<Integer, Integer, Integer>() {
        @Override public Integer call(Integer i1, Integer i2) {
          return i1 + i2;
        }
      }
    );
    // Print the first ten elements of each RDD generated in this DStream to the console
    wordCounts.print();
    
    spark.start();              // Start the computation
    spark.awaitTerminationOrTimeout(5000);   // Wait for the computation to terminate
  }
}
