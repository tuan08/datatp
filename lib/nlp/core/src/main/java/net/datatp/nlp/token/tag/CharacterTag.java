package net.datatp.nlp.token.tag;

import java.util.List;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CharacterTag extends TokenTag {
	final static public String TYPE   = "character" ;
	
	private CharDescriptor[] descriptors ;
	private String suffix ;
	
	public CharacterTag(CharDescriptor[] descriptors, String suffix) {
		this.descriptors = descriptors ;
		this.suffix = suffix ;
	}
	
	public String getSuffix() { return this.suffix ; }
	
	public CharacterTag(List<CharDescriptor> descriptors, String suffix) {
		this.descriptors = descriptors.toArray(new CharDescriptor[descriptors.size()]) ;
		this.suffix = suffix ;
	}
	
	public String getOType() { return TYPE ; }
	
	public CharDescriptor[] getCharDescriptors() { return this.descriptors ; }

	public CharDescriptor getCharDescriptors(char c) { 
		for(CharDescriptor sel : this.descriptors) {
			if(sel.character == c) return sel ;
		}
		return null ; 
	}
	
	public boolean isTypeOf(String type) { return TYPE.equals(type) ; }
	
	public String getInfo() {
		StringBuilder b = new StringBuilder() ;
		b.append(getOType()).append(": {") ;
		for(int i = 0; i < descriptors.length; i++) {
			if(i > 0) b.append(", ") ;
			b.append(descriptors[i].character).append("=").append(descriptors[i].count) ;
		}
		b.append("}") ;
		return b.toString() ;
	}
	
	static public class CharDescriptor {
		final public char character ;
		final public byte count ;
		
		public CharDescriptor(char character, byte count) {
			this.character = character ;
			this.count = count ;
		}
	}
}