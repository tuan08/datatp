package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class URLTag extends WordTag {
	final static public String TYPE = "url" ;
	final static public String[] KNOWN_DOMAINS = {
		".com", ".net", ".edu", ".org", ".mil", ".us", ".uk", ".fr", ".vn", ".ch", ".au"
	} ;
	final static public String[] KNOWN_PROTOCOL = {
		"http://", "https://", "fpt://", "file://"
	} ;
	
	private String url ;
	private String provider ;

	public URLTag(String url) {
		super(TYPE);
		this.url = url ;
	}
	
	public String getURL() { return this.url ; }

	public String getProvider() { return provider ; }
	
	public String getTagValue() { return url ; }
	
	static public boolean isEndWithKnownDomain(String string) {
		for(int i = 0; i < KNOWN_DOMAINS.length; i ++) {
			if(string.endsWith(KNOWN_DOMAINS[i])) return true ;
		}
		return false ;
	}
	
	static public boolean isStartWithKnownProtocol(String string) {
		for(int i = 0; i < KNOWN_PROTOCOL.length; i ++) {
			if(string.startsWith(KNOWN_PROTOCOL[i])) return true ;
		}
		return false ;
	}
}