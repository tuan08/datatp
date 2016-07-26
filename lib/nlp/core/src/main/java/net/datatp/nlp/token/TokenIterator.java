package net.datatp.nlp.token;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenIterator {
	public IToken next() throws TokenException ;
}