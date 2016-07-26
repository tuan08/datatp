package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EmailTag extends WordTag {
	final static public String TYPE = "email" ;
	
	private String email ;
	private String provider ;

	public EmailTag(String email) {
		super(TYPE);
		this.email = email ;
		provider = email.substring(email.indexOf('@') + 1) ;
	}
	
	public String getEmail() { return this.email ; }

	public String getProvider() { return provider ; }
	
	public String getTagValue() { return email ; }
}