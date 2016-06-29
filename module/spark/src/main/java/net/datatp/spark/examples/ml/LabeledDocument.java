package net.datatp.spark.examples.ml;

import java.io.Serializable;

/**
 * Labeled instance type, Spark SQL can infer schema from Java Beans.
 */
@SuppressWarnings("serial")
public class LabeledDocument extends Document implements Serializable {

  private double label;

  public LabeledDocument(long id, String text, double label) {
    super(id, text);
    this.label = label;
  }

  public double getLabel() {
    return this.label;
  }
}
