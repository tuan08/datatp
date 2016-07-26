package net.datatp.nlp.token.analyzer;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.tag.EmailTag;
import net.datatp.nlp.token.tag.WordTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class EmailTokenAnalyzer implements TokenAnalyzer {
  public IToken[] analyze(IToken[] token) throws TokenException {
    for(int i = 0; i < token.length; i++) {
      if(token[i].hasTagType(WordTag.LETTER)) ;
      analyze(token, i) ;
    }
    return token ;
  }

  public void analyze(IToken[] tokens, int pos) {
    if(tokens[pos].hasTagType(WordTag.LETTER)) return  ;
    IToken token = tokens[pos] ;
    char[] buf = token.getNormalizeFormBuf() ;
    int atCounter = 0 ;
    int dotCounter = 0 ;
    int lastDotPos = 0;
    for(int i = 0; i < buf.length; i++) {
      char c = buf[i] ;
      if(c == '.') {
        if(atCounter > 0) {
          dotCounter++ ;
          lastDotPos = i ;
        }
        continue ;
      }
      if(c == '@') {
        atCounter++ ;
        continue ;
      }
      if(c >= 'a' && c <= 'z') continue ;
      if(c >= 'A' && c <= 'Z') continue ;
      if(c >= '0' && c <= '9') continue ;
      if(c == '_' || c == '-') continue ;
      return  ;
    }
    if(atCounter != 1) return ;
    if(dotCounter > 0) {
      for(int i = lastDotPos + 1; i < buf.length; i++) {
        char c = buf[i] ;
        if(c >= 'a' && c <= 'z') continue ;
        if(c >= 'A' && c <= 'Z') continue ;
        return  ;
      }
    }
    token.add(new EmailTag(token.getNormalizeForm())) ;
    return ;
  }
}