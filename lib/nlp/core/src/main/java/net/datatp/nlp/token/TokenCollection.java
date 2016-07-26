package net.datatp.nlp.token;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.util.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenCollection extends IToken {
  private IToken[] token ;

  public TokenCollection(IToken token) {
    this.token = new IToken[] { token } ;
  }

  public TokenCollection(IToken[] token) {
    this.token = token ;
  }

  /**
   * Create a token sequence that is a sub set of token. The token sequence begins at the 
   * specified from and extends to the token at index to - 1. Thus the length of the 
   * token sequence is to - from. 
   */
  public TokenCollection(IToken[] token, int from, int to) {
    this.token = new IToken[to - from] ;
    System.arraycopy(token, from, this.token, 0, this.token.length) ;
  }

  public IToken[] getTokens() { return this.token ; }

  public IToken[] getSingleTokens() { 
    List<IToken> holder = new ArrayList<IToken>(token.length) ;
    for(int i = 0; i < token.length; i++) {
      if(token[i] instanceof TokenCollection) {
        TokenCollection collection = (TokenCollection) token[i] ;
        for(IToken sel : collection.getTokens()) holder.add(sel) ;
      } else {
        holder.add(token[i]) ;
      }
    }
    return holder.toArray(new IToken[holder.size()]); 
  }

  public String[] getWord() { 
    List<String> holder = new ArrayList<String>() ;
    for(int i = 0; i < token.length; i++) {
      for(String sel : token[i].getWord()) holder.add(sel) ;
    }
    return holder.toArray(new String[holder.size()]) ; 
  }

  public String getOriginalForm() { 
    return getOriginalForm(0, token.length) ;
  }

  public String getOriginalForm(int from, int to) { 
    StringBuilder b = new StringBuilder() ;
    for(int i = from; i < to; i++) {
      String origForm = token[i].getOriginalForm() ;
      if(origForm.length() == 1) {
        if(b.length() > 0) {
          b.append(' ') ;
        }
      } else {
        if(b.length() > 0) b.append(' ') ;
      }
      b.append(origForm) ;
    }
    return b.toString() ;
  }

  public String getSegmentTokens() { 
    return this.getSegmentTokens(0, token.length) ;
  }

  public String getSegmentTokens(int from, int to) { 
    StringBuilder b = new StringBuilder() ;
    for(int i = from; i < to; i++) {
      String origForm = token[i].getOriginalForm() ;
      if(b.length() > 0) b.append(" | ") ;
      b.append(origForm) ;
    }
    return b.toString() ;
  }

  public String getNormalizeForm() { 
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < token.length; i++) {
      String normForm = token[i].getNormalizeForm() ;
      if(normForm.length() == 1) {
        char punc = normForm.charAt(0) ;
        if(CharacterSet.isIn(punc, CharacterSet.END_SENTENCE)) {
        } else if(CharacterSet.isIn(punc, CharacterSet.QUOTTATION)) {
        } else if(b.length() > 0) {
          b.append(' ') ;
        }
      } else {
        if(b.length() > 0) b.append(' ') ;
      }
      b.append(normForm) ;
    }
    return b.toString() ;
  }

  public char[] getNormalizeFormBuf() { 
    return getNormalizeForm().toCharArray() ; 
  }

  public void analyze(TokenAnalyzer analyzer) throws TokenException {
    token = analyzer.analyze(token) ;
  }

  public void analyze(TokenAnalyzer[] analyzer) throws TokenException {
    for(TokenAnalyzer sel : analyzer) {
      this.token = sel.analyze(this.token) ;
    }
  }
}