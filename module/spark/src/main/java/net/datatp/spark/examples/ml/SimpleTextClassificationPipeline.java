package net.datatp.spark.examples.ml;

import java.util.List;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import com.google.common.collect.Lists;

/**
 * A simple text classification pipeline that recognizes "spark" from input text. It uses the Java
 * bean classes {@link LabeledDocument} and {@link Document} defined in the Scala counterpart of
 * this example {@link SimpleTextClassificationPipeline}. Run with
 * <pre>
 * bin/run-example ml.SimpleTextClassificationPipeline
 * </pre>
 */
public class SimpleTextClassificationPipeline {

  public static void main(String[] args) {
    //SparkConf conf = new SparkConf().setAppName("SimpleTextClassificationPipeline");
    //JavaSparkContext jsc = new JavaSparkContext(conf);
    JavaSparkContext jsc = new JavaSparkContext("local", "SimpleTextClassificationPipeline");
    SQLContext jsql = new SQLContext(jsc);

    // Prepare training documents, which are labeled.
    List<LabeledDocument> localTraining = Lists.newArrayList(
      new LabeledDocument(0L, "a b c d e spark", 1.0),
      new LabeledDocument(1L, "b d", 0.0),
      new LabeledDocument(2L, "spark f g h", 1.0),
      new LabeledDocument(3L, "hadoop mapreduce", 0.0));
    Dataset<Row> training = jsql.createDataFrame(jsc.parallelize(localTraining), LabeledDocument.class);

    // Configure an ML pipeline, which consists of three stages: tokenizer, hashingTF, and lr.
    Tokenizer tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words");
    HashingTF hashingTF = new HashingTF().setNumFeatures(1000).setInputCol(tokenizer.getOutputCol()).setOutputCol("features");
    LogisticRegression lr = new LogisticRegression().setMaxIter(10).setRegParam(0.001);
    Pipeline pipeline = new Pipeline().setStages(new PipelineStage[] {tokenizer, hashingTF, lr});

    // Fit the pipeline to training documents.
    PipelineModel model = pipeline.fit(training);

    // Prepare test documents, which are unlabeled.
    List<Document> localTest = Lists.newArrayList(
      new Document(4L, "spark i j k"),
      new Document(5L, "l m n"),
      new Document(6L, "spark hadoop spark"),
      new Document(7L, "apache hadoop"));
    Dataset<Row> test = jsql.createDataFrame(jsc.parallelize(localTest), Document.class);

    // Make predictions on test documents.
    Dataset<Row> predictions = model.transform(test);
    for (Row r: predictions.select("id", "text", "probability", "prediction").collectAsList()) {
      System.out.println("(" + r.get(0) + ", " + r.get(1) + ") --> prob=" + r.get(2)
          + ", prediction=" + r.get(3));
    }

    jsc.stop();
  }
}

