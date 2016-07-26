package net.datatp.nlp.vi.token.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.tag.NameTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNNameTokenAnalyzer implements TokenAnalyzer {
  static public HashSet<String> LASTNAMES  ;
  static {
    String[] LNAME = {
        "nguyễn", "lý", "trần", "lê", "phạm", "phan", "hồ", "đặng", "bùi",
        "trương", "đỗ", "đào", "hoàng", "vũ", "châu", "mai", "triệu",
        "dương", "trịnh", "đoàn", "đinh", "trình", "lưu"
    } ;
    LASTNAMES = new HashSet<String>(LNAME.length + 10) ;
    for(String sel : LNAME) LASTNAMES.add(sel) ;
  }

  public IToken[] analyze(IToken[] token) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    int idx =  0;
    while(idx < token.length) {
      IToken tryToken = token[idx] ;
      String orig = tryToken.getOriginalForm() ;
      if(Character.isLowerCase(orig.charAt(0))) {
        holder.add(tryToken) ;
        idx++ ;
        continue ;
      }
      if(!LASTNAMES.contains(tryToken.getNormalizeForm())) {
        holder.add(tryToken) ;
        idx++ ;
        continue ;
      }
      int limitIdx = idx +1 ;
      while(limitIdx < token.length) {
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
        Token newToken = new Token(token, idx, limitIdx) ;
        newToken.add(new NameTag(NameTag.VNNAME, newToken.getOriginalForm())) ;
        holder.add(newToken) ;
        idx = limitIdx ;
      } else {
        holder.add(tryToken) ;
        idx++ ;
      }
    }
    return holder.toArray(new IToken[holder.size()]) ;
  }
}