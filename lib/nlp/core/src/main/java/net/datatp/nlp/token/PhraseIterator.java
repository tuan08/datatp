package net.datatp.nlp.token;

import net.datatp.nlp.util.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PhraseIterator implements TokenSequenceIterator {
  private IToken[] tokens ;
  private int currentPos ;

  public PhraseIterator(IToken[] tokens) {
    this.tokens = tokens ;
  }

  public TokenCollection next() throws TokenException {
    if(currentPos >= tokens.length) return null ;
    int from = currentPos, count = 0 ;
    while(currentPos < tokens.length) {
      IToken sel = tokens[currentPos++] ;
      String normForm = sel.getNormalizeForm() ;
      if(normForm.length() == 1) {
        char punc = normForm.charAt(0) ;
        if(CharacterSet.isIn(punc, CharacterSet.END_SENTENCE)) {
          break ;
        }
        if(count > 15 && (punc == ',' || CharacterSet.isIn(punc, CharacterSet.QUOTTATION))) {
          break ;
        }
        if(count > 50) break ; 
      }
      count++ ;
    }
    TokenCollection tCollection = new TokenCollection(tokens, from, currentPos);
    return tCollection;
  }
}
