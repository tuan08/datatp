package net.datatp.nlp.token;

import net.datatp.nlp.util.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SentenceTokenizer implements TokenSequenceIterator {
  private IToken[] tokens ;
  private int currentPos ;

  public SentenceTokenizer(IToken[] tokens) {
    this.tokens = tokens ;
  }

  public TokenCollection next() throws TokenException {
    if(currentPos >= tokens.length) return null ;
    int from = currentPos ;
    while(currentPos < tokens.length) {
      IToken sel = tokens[currentPos++] ;
      String normForm = sel.getNormalizeForm() ;
      if(normForm.length() == 1) {
        if(CharacterSet.isIn(normForm.charAt(0), CharacterSet.END_SENTENCE)) {
          break ;
        }
      }
    }
    return new TokenCollection(tokens, from, currentPos);
  }
}