package net.datatp.nlp.token;
/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenSequenceIterator extends TokenIterator {
	public TokenCollection next() throws TokenException ;
}
