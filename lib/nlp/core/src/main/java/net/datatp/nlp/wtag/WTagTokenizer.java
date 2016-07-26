package net.datatp.nlp.wtag;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.TokenIterator;
import net.datatp.nlp.util.CharacterSet;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WTagTokenizer implements TokenIterator {
  static char[] SEPARATOR = CharacterSet.merge(CharacterSet.BLANK, new char[]{'\f', '\r', '\0', 13 }) ;

  private char[] separator ;
  private char[] ignoreSeparator ;

  private char[] buf ;
  private int currPos ;

  public WTagTokenizer(String string) {
    this.buf = string.toCharArray() ;
    this.separator = SEPARATOR ;
    this.ignoreSeparator = SEPARATOR ;
  }

  public IToken[] allTokens() throws TokenException {
    ArrayList<IToken> holder = new ArrayList<IToken>() ;
    IToken token = null ;
    while((token = next()) != null) holder.add(token) ;
    return holder.toArray(new IToken[holder.size()]) ;
  }

  public IToken next() throws TokenException {
    if(currPos >= buf.length) return null ;
    while(currPos < buf.length && CharacterSet.isIn(buf[currPos], ignoreSeparator)) {
      currPos++ ;
    }
    if(currPos >= buf.length) return null ;

    if(CharacterSet.isIn(buf[currPos], separator) || buf[currPos] == '\n') {
      String word = new String(new char[] {buf[currPos]}) ;
      currPos++ ;
      Token ret = new Token(word) ;
      ret.add(new WTagBoundaryTag(StringUtil.EMPTY_ARRAY));
      return ret ;
    }
    int startPos = currPos ;
    while(currPos < buf.length) {
      if(buf[currPos] == ':') {
        int nextPos = currPos + 1;
        if(nextPos < buf.length && buf[nextPos] == '{') {
          break ;
        } 
      }
      currPos++ ;
    }
    char[] sub = new char[currPos - startPos] ;
    System.arraycopy(buf, startPos, sub, 0, sub.length) ;
    Token ret = new Token(new String(sub), true);
    ret.add(getBoundaryTag()) ;
    return ret ;
  }

  private WTagBoundaryTag getBoundaryTag() {
    int start = currPos, end = buf.length ;
    while(currPos < buf.length) {
      if(buf[currPos] == '{') {
        start = currPos + 1;
      } else if(buf[currPos] == '}') {
        end = currPos ;
        break ;
      }
      currPos++ ;
    }
    currPos++ ;
    char[] sub = new char[end - start] ;
    System.arraycopy(buf, start, sub, 0, sub.length) ;
    List<String> params = StringUtil.split(sub, ',') ;
    return new WTagBoundaryTag(params.toArray(new String[params.size()])) ;
  }
}