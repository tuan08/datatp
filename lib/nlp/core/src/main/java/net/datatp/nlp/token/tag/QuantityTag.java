package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class QuantityTag extends TokenTag {
  private String otype ;
  private String unit ;

  public QuantityTag(String typeName) { this.otype = typeName; }

  public String getUnit() { return this.unit ; }
  public void setUnit(String unit) { this.unit = unit ; }

  public String getOType() { return otype ; }

  public boolean isTypeOf(String type) {
    if("quantity".equals(type)) return true ;
    return otype.equals(type) ;
  }
}