package net.datatp.nlp.token;

import java.util.List;

import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Token extends IToken {
  private String[] word ;
  private String   orginalForm ;
  private String   normalizeForm ;
  private char[]   normalizeFormBuf ;

  public Token(String originalForm) {
    reset(originalForm, false) ;
  }

  public Token(String originalForm, boolean split) {
    reset(originalForm, split) ;
  }

  public Token(IToken[] token, int from, int to) {
    this.orginalForm = newTokenString(token, from, to) ;
    this.normalizeForm = orginalForm.toLowerCase() ;
    List<String> holder = StringUtil.split(this.normalizeForm, ' ') ;
    this.word = holder.toArray(new String[holder.size()]) ;
  }

  public Token(String[] token, int from, int to) {
    StringBuilder b = new StringBuilder() ;
    for(int i = from; i < to ; i++) {
      String oform = token[i] ;
      if(i > from) {
        b.append(' ') ;
      }
      b.append(oform) ;

    }
    this.orginalForm = b.toString() ;
    this.normalizeForm = orginalForm.toLowerCase() ;
    List<String> holder = StringUtil.split(this.normalizeForm, ' ') ;
    this.word = holder.toArray(new String[holder.size()]) ;
  }

  public String[] getWord() { return word ; }

  public String getOriginalForm() { return this.orginalForm ; }

  public String getNormalizeForm() { return this.normalizeForm ; }

  public char[] getNormalizeFormBuf() { 
    if(normalizeFormBuf == null) normalizeFormBuf = normalizeForm.toCharArray() ;
    return normalizeFormBuf ; 
  }

  public void reset(String text, boolean split) {
    this.orginalForm = text ;
    this.normalizeForm = orginalForm.toLowerCase() ;
    if(split) {
      List<String> holder = StringUtil.split(this.normalizeForm, ' ') ;
      this.word = holder.toArray(new String[holder.size()]) ;
    } else  {
      this.word = new String[] {this.normalizeForm} ;
    }
  }

  public void reset(String text) {
    reset(text, false);
  }

  static public String newTokenString(IToken[] token, int from, int to) {
    if(from + 1 == to) return token[from].getOriginalForm() ;

    StringBuilder b = new StringBuilder() ;
    for(int i = from; i < to ; i++) {
      String oform = token[i].getOriginalForm() ;
      if(i > from) {
        b.append(' ') ;
      }
      b.append(oform) ;

    }
    return b.toString() ;
  }
}