package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class NumberTag extends QuantityTag {
  final static public String TYPE = "frequency" ;
  private double value ;

  public NumberTag(double value) {
    super("frequency");
    this.value = value ;
  }

  public double getValue() { return this.value ; }

  public String getTagValue() { return Double.toString(value) ; }
}
