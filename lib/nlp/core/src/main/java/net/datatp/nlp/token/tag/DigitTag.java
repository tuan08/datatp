package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DigitTag extends QuantityTag {
	final static public String TYPE = "digit" ;
	
	private String value ;
	
	public DigitTag(String value) {
	  super("digit");
	  this.value = value ;
  }
	
	public Long getLongValue() { 
		try {
			return Long.parseLong(value) ;
		} catch(Throwable t) {
			return null ;
		}
  }
	
	public String getTagValue() { return value ; }
}
