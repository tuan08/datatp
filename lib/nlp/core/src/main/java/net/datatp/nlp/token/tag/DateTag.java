package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DateTag extends TokenTag {
  final static public String TYPE = "date" ;

  private String date ;

  public DateTag(String date) { this.date = date ; }

  public String getDate() { return this.date ; }

  public String getTagValue() { return date ; }

  public String getOType() { return TYPE ; }

  public boolean isTypeOf(String type) { return TYPE.equals(type); }
}