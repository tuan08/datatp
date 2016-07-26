package net.datatp.nlp.token.analyzer;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenException;

/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TokenAnalyzer {
	static public TokenAnalyzer NONE = new TokenAnalyzer() {
    public IToken[] analyze(IToken[] unit) throws TokenException {
	    return unit;
    }
	};
	
	public IToken[] analyze(IToken[] unit) throws TokenException ;
}