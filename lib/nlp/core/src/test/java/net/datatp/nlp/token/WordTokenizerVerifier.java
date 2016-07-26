package net.datatp.nlp.token;

import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTokenizerVerifier {
  private TokenAnalyzer[] analyzer ;

  public WordTokenizerVerifier(TokenAnalyzer[] analyzer) { 
    this.analyzer = analyzer ;
  }

  public void verify(String text, String ... expect) throws Exception {
    TokenVerifier[] verifier = new TokenVerifier[expect.length] ;
    for(int i = 0; i < verifier.length; i++) {
      verifier[i] = new TokenVerifier(expect[i].trim()) ;
    }
    WordTokenizer tokenizer = new WordTokenizer(text) ;
    IToken[] token = tokenizer.allTokens() ;
    for(TokenAnalyzer sel : analyzer) {
      token = sel.analyze(token) ;
    }
    for(int i = 0; i < token.length; i++) {
      verifier[i].verify(token[i]) ;
    }
  }

  public void verifyFail(String text, String ... expect) throws Exception {
    try {
      verify(text, expect) ;
    } catch(RuntimeException ex) {
      return ;
    }
    throw new RuntimeException("'" + text + "' should not have {" + StringUtil.joinStringArray(expect) + "}") ;
  }
}