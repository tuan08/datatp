package net.datatp.spark.examples.ml;

import java.io.Serializable;

public class Document implements Serializable {

  private long id;
  private String text;

  public Document(long id, String text) {
    this.id = id;
    this.text = text;
  }

  public long getId() {
    return this.id;
  }

  public String getText() {
    return this.text;
  }
}
