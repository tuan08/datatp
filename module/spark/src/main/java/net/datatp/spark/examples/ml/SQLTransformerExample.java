package net.datatp.spark.examples.ml;

import java.util.Arrays;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.feature.SQLTransformer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
// $example off$
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class SQLTransformerExample {
  public static void main(String[] args) {

    //SparkConf conf = new SparkConf().setAppName("SQLTransformerExample");
    //JavaSparkContext jsc = new JavaSparkContext(conf);
    
    JavaSparkContext jsc = new JavaSparkContext("local", "SQLTransformerExample");
    SQLContext sqlContext = new SQLContext(jsc);

    // $example on$
    JavaRDD<Row> jrdd = jsc.parallelize(Arrays.asList(
      RowFactory.create(0, 1.0, 3.0),
      RowFactory.create(2, 2.0, 5.0)
    ));
    StructType schema = new StructType(new StructField [] {
      new StructField("id", DataTypes.IntegerType, false, Metadata.empty()),
      new StructField("v1", DataTypes.DoubleType, false, Metadata.empty()),
      new StructField("v2", DataTypes.DoubleType, false, Metadata.empty())
    });
    Dataset<Row> df = sqlContext.createDataFrame(jrdd, schema);

    SQLTransformer sqlTrans = new SQLTransformer().setStatement(
      "SELECT *, (v1 + v2) AS v3, (v1 * v2) AS v4 FROM __THIS__");

    sqlTrans.transform(df).show();
    // $example off$
  }

}
