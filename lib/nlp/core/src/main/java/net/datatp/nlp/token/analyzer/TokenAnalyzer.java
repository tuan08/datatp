package net.datatp.nlp.token.analyzer;

import net.datatp.nlp.NLP;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenException;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class TokenAnalyzer {
  public void configure(NLP nlp) {
  }
  
  abstract public IToken[] analyze(IToken[] unit) throws TokenException ;
}