package net.datatp.spark.examples.ml;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.ml.tuning.TrainValidationSplit;
import org.apache.spark.ml.tuning.TrainValidationSplitModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

/**
 * A simple example demonstrating model selection using TrainValidationSplit.
 *
 * The example is based on {@link org.apache.spark.examples.ml.JavaSimpleParamsExample}
 * using linear regression.
 *
 * Run with
 * {{{
 * bin/run-example ml.JavaTrainValidationSplitExample
 * }}}
 */
public class JavaTrainValidationSplitExample {

  public static void main(String[] args) {
    //SparkConf conf = new SparkConf().setAppName("JavaTrainValidationSplitExample");
    //JavaSparkContext jsc = new JavaSparkContext(conf);
    JavaSparkContext jsc = new JavaSparkContext("local", "JavaTrainValidationSplitExample");
    SQLContext jsql = new SQLContext(jsc);

    Dataset<Row> data = jsql.read().format("libsvm").load("src/data/sample_libsvm_data.txt");

    // Prepare training and test data.
    Dataset<Row>[] splits = data.randomSplit(new double [] {0.9, 0.1}, 12345);
    Dataset<Row> training = splits[0];
    Dataset<Row> test = splits[1];

    LinearRegression lr = new LinearRegression();

    // We use a ParamGridBuilder to construct a grid of parameters to search over.
    // TrainValidationSplit will try all combinations of values and determine best model using
    // the evaluator.
    ParamMap[] paramGrid = new ParamGridBuilder()
      .addGrid(lr.regParam(), new double[] { 0.1, 0.02})
      .addGrid(lr.fitIntercept())
      .addGrid(lr.elasticNetParam(), new double[] {0.0, 0.5, 1.0})
      .build();

    // In this case the estimator is simply the linear regression.
    // A TrainValidationSplit requires an Estimator, a set of Estimator ParamMaps, and an Evaluator.
    TrainValidationSplit trainValidationSplit = 
      new TrainValidationSplit().setEstimator(lr).setEvaluator(new RegressionEvaluator()).setEstimatorParamMaps(paramGrid);

    //80% of the data will be used for training and the remaining 20% for validation.
    trainValidationSplit.setTrainRatio(0.8);

    // Run train validation split, and choose the best set of parameters.
    TrainValidationSplitModel model = trainValidationSplit.fit(training);

    // Make predictions on test data. model is the model with combination of parameters that performed best.
    model.transform(test).select("features", "label", "prediction").show();
    jsc.stop();
    System.out.println("Done...");
  }
}
