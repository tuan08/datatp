package net.datatp.nlp.token;

import net.datatp.nlp.token.analyzer.TokenAnalyzer;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TextSegmenter {
  private TokenAnalyzer[] analyzer ;

  public TextSegmenter(TokenAnalyzer ... analyzer) {
    this.analyzer = analyzer ;
  }

  public IToken[] segment(String text) throws TokenException {
    IToken[] token = new WordTokenizer(text).allTokens() ;
    if(analyzer != null) {
      for(TokenAnalyzer sel : analyzer) {
        token = sel.analyze(token) ;
      }
    }
    return token ;
  }
}
