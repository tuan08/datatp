package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.PhoneTag;
import net.datatp.nlp.vi.token.analyzer.VNPhoneNumberUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PhoneNumberChunker extends QueryChunker {
  public PhoneNumberChunker(MatcherResourceFactory mrFactory) throws Exception {
    super(mrFactory) ;
    String phoneNumExp =  "(\\d{1,4}[.\\-]?)+" ;

    String regexWithBracket = "regex{\\(} .0. regex{\\+?\\d{2,4}} .0. regex{\\)}";

    defineMatches(
        // The format start with bracket followed by two or three frequency groups
        "/ " + regexWithBracket + " .1. regex{\\d{2,4}} .0. regex{\\d{2,4}} .0. regex{\\d{2,4}}" ,
        "/ " + regexWithBracket + " .1. regex{\\d{2,4}} .0. regex{\\d{2,4}}" ,
        // The format start with bracket followed by a common format
        "/ " + regexWithBracket + " .0. regex{[0-9.\\-]+}" , 
        // The format contains spaces between frequency groups
        "/ regex{\\d{2,4}} .0. regex{\\d{6,8}}" ,
        "/ regex{\\d{2,4}} .0. regex{\\d{2,4}} .0. regex{\\d{2,4}}" , 
        // Common format
        "/ regex{(\\d{1,4}[.\\-]?){2,5}}"
        ) ;
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
    IToken set = createChunking(token, from, to) ;
    String number = set.getOriginalForm();
    boolean ignore = false ;
    if(number.length() < 8) ignore = true ; 
    else if(number.endsWith("000")) ignore = true ;
    if(ignore) {
      for(int i = from; i < to; i++) holder.add(token[i]) ;
      return ;
    }
    String provider = VNPhoneNumberUtil.getMobileProvider(number);
    set.add(new PhoneTag(number, provider, "phone"));
    holder.add(set) ;
  }
}