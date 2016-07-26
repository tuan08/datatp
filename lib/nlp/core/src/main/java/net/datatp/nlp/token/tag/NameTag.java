package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class NameTag extends TokenTag {
  final static public String VNNAME = "vnname" ;
  final static public String NAME   = "name" ;

  private String otype ;
  private String value ;

  public NameTag(String otype, String value) {
    this.otype = otype ;
    this.value = value ;
  }

  public String getOType() { return this.otype; }

  public String getTagValue() { return value ; }

  public boolean isTypeOf(String type) {
    if("word".equals(type)) return true ;
    return otype.equals(type) ;
  }
}
