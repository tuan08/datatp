package net.datatp.nlp.ws;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.PunctuationTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class NGram {
  private int    numberOfGram ;
  private String token ;

  public NGram(IToken[] token, int from, int to) {
    this.numberOfGram =  to - from ;
    StringBuilder b = new StringBuilder() ;
    for(int i = from; i < to; i++) {
      if(i > from) b.append(' ') ;
      b.append(token[i].getNormalizeForm()) ;
    }
    this.token = b.toString() ;
  }

  public NGram(String[] token, int from, int to) {
    this.numberOfGram =  to - from ;
    StringBuilder b = new StringBuilder() ;
    for(int i = from; i < to; i++) {
      if(i > from) b.append(' ') ;
      b.append(token[i]) ;
    }
    this.token = b.toString() ;
  }

  public int getNumberOfGram() { return this.numberOfGram ; }

  public String getToken() { return this.token ; }

  static public NGram[] ngrams(IToken[] token, int maxNGram) {
    List<NGram> holder = new ArrayList<NGram>() ;
    for(int i = 0; i < token.length; i++) {
      int limit = i + maxNGram;
      if(limit > token.length) limit = token.length ; 
      for(int j = i; j < limit; j++) {
        PunctuationTag ptag = token[i].getFirstTagType(PunctuationTag.class) ;
        if(ptag != null) break ;
        NGram ngram = new NGram(token, i, j + 1) ;
        holder.add(ngram) ;
      }
    }
    return holder.toArray(new NGram[holder.size()]) ;
  }

  static public NGram[] ngrams(String[] token, int maxNGram) {
    List<NGram> holder = new ArrayList<NGram>() ;
    for(int i = 0; i < token.length; i++) {
      int limit = i + maxNGram;
      if(limit > token.length) limit = token.length ; 
      for(int j = i; j < limit; j++) {
        NGram ngram = new NGram(token, i, j + 1) ;
        holder.add(ngram) ;
      }
    }
    return holder.toArray(new NGram[holder.size()]) ;
  }

  static public void main(String[] args) {
    String text = "4 chiếc máy bay cá nhân do Công ty Hành Tinh Xanh nhập sẽ mất nhiều thời gian hoàn tất thủ tục bay";
    String[] token = text.split(" ") ;
    NGram[] ngram = NGram.ngrams(token, 3) ;
    for(int i = 0; i < ngram.length; i++) {
      System.out.println(ngram[i].getToken());
    }
  }
}
