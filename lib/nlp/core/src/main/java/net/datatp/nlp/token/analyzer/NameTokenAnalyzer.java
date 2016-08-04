package net.datatp.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.tag.NameTag;
import net.datatp.nlp.util.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class NameTokenAnalyzer extends TokenAnalyzer {
  public IToken[] analyze(IToken[] token) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    IToken previousToken = null ;
    int idx =  0;
    while(idx < token.length) {
      IToken tryToken = token[idx] ;
      String orig = tryToken.getOriginalForm() ;
      if(tryToken.getWord().length > 1 || !Character.isUpperCase(orig.charAt(0))) {
        holder.add(tryToken) ;
        idx++ ;
        continue ;
      }
      int limitIdx = idx +1 ;
      while(limitIdx < idx + 5 && limitIdx < token.length) {
        IToken nextToken = token[limitIdx] ;
        if(nextToken.getWord().length == 1) {
          String string = nextToken.getOriginalForm() ;
          char fistLetter = string.charAt(0) ;
          if(Character.isUpperCase(fistLetter)) {
            limitIdx++ ;
          } else {
            break ;
          }
        } else {
          break ;
        }
      }
      if(limitIdx - idx > 1) {
        IToken newToken = new Token(token, idx, limitIdx) ;
        newToken.add(new NameTag(NameTag.NAME, newToken.getOriginalForm())) ;
        holder.add(newToken) ;
        idx = limitIdx ;
      } else {
        if(previousToken != null) {
          char firstChar = previousToken.getOriginalForm().charAt(0) ;
          if(!CharacterSet.isIn(firstChar, CharacterSet.END_SENTENCE)) {
            tryToken.add(new NameTag(NameTag.NAME, tryToken.getOriginalForm())) ;
          }
        }
        holder.add(tryToken) ;
        idx++ ;
      }
      previousToken = tryToken ;
    }
    return holder.toArray(new IToken[holder.size()]) ;
  }
}