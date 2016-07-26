package net.datatp.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.tag.PunctuationTag;
import net.datatp.nlp.util.CharacterSet;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PunctuationTokenAnalyzer implements TokenAnalyzer {
  final static public PunctuationTokenAnalyzer INSTANCE = new PunctuationTokenAnalyzer() ;
  static HashSet<String> ABBRS = new HashSet<String>() ;
  static {
    ABBRS.add("mr.") ;
    ABBRS.add("dr.") ;
    ABBRS.add("prof.") ;
    ABBRS.add("dep.") ;
    ABBRS.add("tp.") ;
    ABBRS.add("n.") ;
    ABBRS.add("q.") ;
  }

  public IToken[] analyze(IToken[] tokens) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>(tokens.length + 25) ;
    for(IToken sel : tokens) {
      if(sel instanceof TokenCollection) {
        holder.add(sel) ;
        continue ;
      } 

      String normForm = sel.getNormalizeForm() ;
      if(normForm.length() == 0) continue ;

      if(normForm.length() > 1) {
        char lastChar = normForm.charAt(normForm.length() -1 ) ;
        if(lastChar == ',' || lastChar == ':' || isSplitEndSentence(lastChar, normForm) && !ABBRS.contains(normForm)) {
          int to = normForm.length()- 1 ;
          if(normForm.endsWith("...")) to = normForm.length() - 3 ;
          String origForm = sel.getOriginalForm() ;
          holder.add(new Token(origForm.substring(0, to))) ;
          Token puncToken = new Token(new String(origForm.substring(to))) ;
          puncToken.add(PunctuationTag.INSTANCE) ;
          holder.add(puncToken) ;
        } else {
          holder.add(sel) ;
        }
      } else {
        char c = normForm.charAt(0) ;
        if(PunctuationTag.isPunctuation(c)) {
          sel.add(PunctuationTag.INSTANCE) ;
          holder.add(sel) ;
        } else {
          holder.add(sel) ;
        }
      }
    }
    return holder.toArray(new IToken[holder.size()]);
  }

  private boolean isSplitEndSentence(char lastChar, String normForm) {
    if(!CharacterSet.isIn(lastChar, CharacterSet.END_SENTENCE)) return false ;
    if(lastChar == '.') {
      int limit = 5 ;
      if(normForm.length() - 1 < limit) limit = normForm.length() - 1 ;
      for(int i = 0; i < limit; i++) {
        if(Character.isUpperCase(normForm.charAt(i))) return false ;
      }
    }
    return true ;
  }
}
