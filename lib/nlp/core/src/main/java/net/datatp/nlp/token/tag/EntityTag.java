package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityTag extends TokenTag {
	private String otype ;
	private String value ;
	
	public EntityTag(String otype, String value) {
	  this.otype = otype ;
	  this.value = value ;
  }

	public String getOType() { return this.otype; }
	
	public String getTagValue() { return value ; }
	
	public boolean isTypeOf(String type) { return otype.equals(type) ; }
}
