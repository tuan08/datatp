package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class PhoneTag extends TokenTag {
  final static public String TYPE = "phone" ;

  private String number ;
  private String type ;
  private String provider ;

  public PhoneTag(String number, String provider, String type) {
    this.number    = number ;
    this.provider  = provider ;
    this.type = type ;
  }

  public String getNumber() { return this.number  ; }

  public String getProvider() { return this.provider ; }

  public String getType() { return this.type ; }

  public String getTagValue() { return number ; }

  public String getOType() { return TYPE ; }

  public boolean isTypeOf(String type) {
    return TYPE.equals(type);
  }
}