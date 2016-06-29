package net.datatp.spark.examples.ml;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.linalg.SparseVector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;


public class FeaturesExtractorExample {
  public static void main(String[] args) {
    SparkSession spark = SparkSession.builder().master("local").appName("JavaTfIdfExample").getOrCreate();
    // $example on$
    List<Row> data = Arrays.asList(
      RowFactory.create(0, "Hi I heard about Spark"),
      RowFactory.create(0, "I wish Java could use case classes"),
      RowFactory.create(1, "Logistic regression models are neat")
    );
    StructType schema = new StructType(new StructField[]{
        new StructField("label", DataTypes.IntegerType, false, Metadata.empty()),
        new StructField("sentence", DataTypes.StringType, false, Metadata.empty())
    });
    Dataset<Row> sentenceData = spark.createDataFrame(data, schema);
    Tokenizer tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words");
    Dataset<Row> wordsData = tokenizer.transform(sentenceData);
    int numFeatures = 20;
    HashingTF hashingTF = new HashingTF()
        .setInputCol("words")
        .setOutputCol("rawFeatures")
        .setNumFeatures(numFeatures);
    Dataset<Row> featurizedData = hashingTF.transform(wordsData);
    // alternatively, CountVectorizer can also be used to get term frequency vectors

    IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
    IDFModel idfModel = idf.fit(featurizedData);
    Dataset<Row> rescaledData = idfModel.transform(featurizedData);
    for (Row r : rescaledData.select("features", "label").takeAsList(3)) {
      SparseVector features = r.getAs(0);
      Integer label = r.getInt(1);
      System.out.println(features);
      System.out.println(label);
    }
    // $example off$

    spark.stop();
  }
}
