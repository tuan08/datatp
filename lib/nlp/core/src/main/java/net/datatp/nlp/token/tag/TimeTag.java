package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TimeTag extends TokenTag {
  final static public String TYPE = "time" ;

  private String time ;

  public TimeTag(String time) { this.time = time ;}

  public String getTime() { return this.time ; }

  public String getTagValue() { return time ; }

  public String getOType() { return TYPE ; }

  public boolean isTypeOf(String type) { return TYPE.equals(type); }
}