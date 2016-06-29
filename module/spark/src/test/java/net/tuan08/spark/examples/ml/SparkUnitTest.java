package net.tuan08.spark.examples.ml;

import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SparkUnitTest {
  private SparkSession spark ;

  @Before
  public void setUp() {
    spark = 
      SparkSession.builder().master("local").appName("JavaDecisionTreeClassificationExample").getOrCreate();
  }

  @After
  public void tearDown() {
    spark.stop();
    spark = null;
  }
  
  @Test
  public void testDecisionTreeClassificationExample() throws Exception {
    DecisionTreeClassificationExample example = new  DecisionTreeClassificationExample();
    example.run(spark);
  }
  
  @Test
  public void testDecisionTreeRegressionExample() throws Exception {
    DecisionTreeRegressionExample example = new DecisionTreeRegressionExample();
    example.run(spark);
  }
  
}
