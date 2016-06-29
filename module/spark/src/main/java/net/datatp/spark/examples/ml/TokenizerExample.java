package net.datatp.spark.examples.ml;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

// $example on$
import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.RegexTokenizer;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
// $example off$

public class TokenizerExample {
  public static void main(String[] args) {
    //SparkConf conf = new SparkConf().setAppName("TokenizerExample");
    //JavaSparkContext jsc = new JavaSparkContext(conf);
    JavaSparkContext jsc = new JavaSparkContext("local", "TokenizerExample");
    SQLContext sqlContext = new SQLContext(jsc);

    // $example on$
    JavaRDD<Row> jrdd = jsc.parallelize(Arrays.asList(
      RowFactory.create(0, "Hi I heard about Spark"),
      RowFactory.create(1, "I wish Java could use case classes"),
      RowFactory.create(2, "Logistic,regression,models,are,neat")
    ));

    StructType schema = new StructType(new StructField[]{
      new StructField("label", DataTypes.IntegerType, false, Metadata.empty()),
      new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
    });

    Dataset<Row> sentenceDataFrame = sqlContext.createDataFrame(jrdd, schema);

    Tokenizer tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");

    Dataset<Row> wordsDataFrame = tokenizer.transform(sentenceDataFrame);
    for (Row r : wordsDataFrame.select("words", "label"). takeAsList(3)) {
      java.util.List<String> words = r.getList(0);
      for (String word : words) System.out.print(word + " ");
      System.out.println();
    }

    RegexTokenizer regexTokenizer = new RegexTokenizer()
      .setInputCol("sentence")
      .setOutputCol("words")
      .setPattern("\\W");  // alternatively .setPattern("\\w+").setGaps(false);
    // $example off$
    jsc.stop();
  }
}
