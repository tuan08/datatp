package net.datatp.nlp.token;

import java.util.ArrayList;

import net.datatp.nlp.util.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTokenizer implements TokenIterator {
  static char[] DEFAULT_SEPARATOR = 
      CharacterSet.merge(CharacterSet.BLANK, CharacterSet.BRACKET,  CharacterSet.NEW_LINE, CharacterSet.QUOTTATION,  new char[] {'|'}) ;
  
  static char[] DEFAULT_IGNORE_SEPARATOR = 
      CharacterSet.merge(CharacterSet.BLANK, new char[]{'\f', '\r', '\0', 13 }) ;

  private char[] separator ;
  private char[] ignoreSeparator ;

  private char[] buf ;
  private int currPos ;

  public WordTokenizer(String string) {
    this.buf = string.toCharArray() ;
    this.separator = DEFAULT_SEPARATOR ;
    this.ignoreSeparator = DEFAULT_IGNORE_SEPARATOR ;
  }

  public WordTokenizer(String string, char[] separator, char[] ignoreChar) {
    this.buf = string.toCharArray() ;
    this.separator = separator ;
    this.ignoreSeparator = ignoreChar ;
  }

  public IToken next() {
    if(currPos >= buf.length) return null ;
    while(currPos < buf.length && CharacterSet.isIn(buf[currPos], ignoreSeparator)) {
      currPos++ ;
    }
    if(currPos >= buf.length) return null ;
    if(CharacterSet.isIn(buf[currPos], separator)) {
      String word = new String(new char[] {buf[currPos]}) ;
      currPos++ ;
      return new Token(word) ;
    }
    int startPos = currPos ;
    while(currPos < buf.length) {
      if(CharacterSet.isIn(buf[currPos], separator)) {
        break ;
      } else {
        currPos++ ;
      }
    }
    char[] sub = new char[currPos - startPos] ;
    System.arraycopy(buf, startPos, sub, 0, sub.length) ;
    return new Token(new String(sub));
  }

  public IToken[] allTokens() throws TokenException {
    ArrayList<IToken> holder = new ArrayList<IToken>() ;
    IToken token = null ;
    while((token = next()) != null) holder.add(token) ;
    return holder.toArray(new IToken[holder.size()]) ;
  }
}
