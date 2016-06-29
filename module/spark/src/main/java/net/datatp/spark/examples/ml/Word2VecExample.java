package net.datatp.spark.examples.ml;

import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.feature.Word2Vec;
import org.apache.spark.ml.feature.Word2VecModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
// $example off$
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Word2VecExample {
  public static void main(String[] args) {
    //SparkConf conf = new SparkConf().setAppName("Word2VecExample");
    //JavaSparkContext jsc = new JavaSparkContext(conf);
    JavaSparkContext jsc = new JavaSparkContext("local", "Word2VecExample");
    SQLContext sqlContext = new SQLContext(jsc);

    //$example on$
    //Input data: Each row is a bag of words from a sentence or document.
    JavaRDD<Row> jrdd = jsc.parallelize(Arrays.asList(
      RowFactory.create(Arrays.asList("Hi I heard about Spark".split(" "))),
      RowFactory.create(Arrays.asList("I wish Java could use case classes".split(" "))),
      RowFactory.create(Arrays.asList("Logistic regression models are neat".split(" ")))
    ));
    
    StructType schema = new StructType(new StructField[] {
      new StructField("text", new ArrayType(DataTypes.StringType, true), false, Metadata.empty())
    });
    Dataset<Row> documentDF = sqlContext.createDataFrame(jrdd, schema);

    // Learn a mapping from words to Vectors.
    Word2Vec word2Vec = new Word2Vec().setInputCol("text").setOutputCol("result").setVectorSize(3).setMinCount(0);
    Word2VecModel model = word2Vec.fit(documentDF);
    Dataset<Row>result = model.transform(documentDF);
    for (Row r : result.select("result").takeAsList(3)) {
      System.out.println(r);
    }
    // $example off$
  }
}
