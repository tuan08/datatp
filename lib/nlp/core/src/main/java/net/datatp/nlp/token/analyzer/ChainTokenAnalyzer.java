package net.datatp.nlp.token.analyzer;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenException;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ChainTokenAnalyzer extends TokenAnalyzer {
  private TokenAnalyzer[] analyzer ;

  public ChainTokenAnalyzer(TokenAnalyzer ... analyzer) {
    this.analyzer = analyzer ;
  }

  public IToken[] analyze(IToken[] tokens) throws TokenException {
    for(TokenAnalyzer sel : analyzer) {
      tokens = sel.analyze(tokens) ;
    }
    return tokens ;
  }
}