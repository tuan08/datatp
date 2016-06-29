package com.neverwinterdp.spark;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

import scala.Tuple2;

public class SparkUnitTest implements Serializable {
  private transient JavaSparkContext sc;
  private transient File tempDir;

  @Before
  public void setUp() {
    sc = new JavaSparkContext("local", "JavaAPISuite");
    tempDir = Files.createTempDir();
    tempDir.deleteOnExit();
  }

  @After
  public void tearDown() {
    sc.stop();
    sc = null;
  }
  
  @Test
  public void sortByKey() {
    List<Tuple2<Integer, Integer>> pairs = new ArrayList<Tuple2<Integer, Integer>>();
    pairs.add(new Tuple2<Integer, Integer>(0, 4));
    pairs.add(new Tuple2<Integer, Integer>(3, 2));
    pairs.add(new Tuple2<Integer, Integer>(-1, 1));

    JavaPairRDD<Integer, Integer> rdd = sc.parallelizePairs(pairs);

    // Default comparator
    JavaPairRDD<Integer, Integer> sortedRDD = rdd.sortByKey();
    Assert.assertEquals(new Tuple2<Integer, Integer>(-1, 1), sortedRDD.first());
    List<Tuple2<Integer, Integer>> sortedPairs = sortedRDD.collect();
    Assert.assertEquals(new Tuple2<Integer, Integer>(0, 4), sortedPairs.get(1));
    Assert.assertEquals(new Tuple2<Integer, Integer>(3, 2), sortedPairs.get(2));

    // Custom comparator
    sortedRDD = rdd.sortByKey(Collections.<Integer>reverseOrder(), false);
    Assert.assertEquals(new Tuple2<Integer, Integer>(-1, 1), sortedRDD.first());
    sortedPairs = sortedRDD.collect();
    Assert.assertEquals(new Tuple2<Integer, Integer>(0, 4), sortedPairs.get(1));
    Assert.assertEquals(new Tuple2<Integer, Integer>(3, 2), sortedPairs.get(2));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void sequenceFile() {
    String outputDir = new File(tempDir, "output").getAbsolutePath();
    
    List<Tuple2<Integer, String>> pairs = Arrays.asList(
        new Tuple2<Integer, String>(1, "a"),
        new Tuple2<Integer, String>(2, "aa"),
        new Tuple2<Integer, String>(3, "aaa")
    );
    
    JavaPairRDD<Integer, String> rdd = sc.parallelizePairs(pairs);

    rdd.mapToPair(new PairFunction<Tuple2<Integer, String>, IntWritable, Text>() {
      @Override
      public Tuple2<IntWritable, Text> call(Tuple2<Integer, String> pair) {
        return new Tuple2<IntWritable, Text>(new IntWritable(pair._1()), new Text(pair._2()));
      }
    }).saveAsHadoopFile(outputDir, IntWritable.class, Text.class, SequenceFileOutputFormat.class);

    // Try reading the output back as an object file
    JavaPairRDD<Integer, String> readRDD = sc.sequenceFile(outputDir, IntWritable.class,
      Text.class).mapToPair(new PairFunction<Tuple2<IntWritable, Text>, Integer, String>() {
        @Override
        public Tuple2<Integer, String> call(Tuple2<IntWritable, Text> pair) {
          return new Tuple2<Integer, String>(pair._1().get(), pair._2().toString());
        }
      }
    );
    List<Tuple2<Integer, String>> collect = readRDD.collect();
    System.out.println(pairs);
    System.out.println(collect);
    Assert.assertEquals(pairs, readRDD.collect());
  }
}
