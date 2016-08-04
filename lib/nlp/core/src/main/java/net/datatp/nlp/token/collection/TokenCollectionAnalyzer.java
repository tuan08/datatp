package net.datatp.nlp.token.collection;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenCollectionAnalyzer {
	public TokenCollection[] analyze(IToken[] tokens) throws TokenException ;
}