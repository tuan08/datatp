package net.datatp.nlp.vi.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.Token;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.tag.DigitTag;
import net.datatp.nlp.token.tag.PhoneTag;
import net.datatp.nlp.token.tag.WordTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNPhoneTokenAnalyzer extends TokenAnalyzer {
  final static String[] IGNORE_TYPE = {WordTag.WLETTER.getOType()} ;

  public IToken[] analyze(IToken[] token) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    for(int i = 0; i < token.length; i++) {
      if(token[i].hasTagType(IGNORE_TYPE)) {
        holder.add(token[i]) ;
        continue ;
      }

      String norm = token[i].getNormalizeForm() ;
      if(norm.length() == 1 && norm.charAt(0) == '(') {
        if(i + 3 < token.length && 
            token[i + 1].hasTagType(DigitTag.TYPE) && 
            token[i + 2].getNormalizeForm().charAt(0) == ')') {
          String test = token[i + 1].getNormalizeForm() + token[i + 3].getNormalizeForm() ;
          test = VNPhoneNumberUtil.normalize(test) ;
          String areaCode = VNPhoneNumberUtil.getAreaCode(test) ;
          if(areaCode != null && test.length() >= 10 && test.length() < 12) {
            IToken newToken = new Token(token, i, i + 4) ;
            newToken.add(new PhoneTag(test, "VNPT", "line")) ;
            holder.add(newToken) ;
            i += 3 ;
            continue ;
          }
        }
      } 

      if(token[i].hasTagType(DigitTag.TYPE) && norm.length() < 5 && i + 1 < token.length) {
        String test = norm + token[i + 1].getNormalizeForm() ;
        String areaCode = VNPhoneNumberUtil.getAreaCode(test) ;
        if(areaCode != null) {
          IToken newToken = new Token(token, i, i + 2) ;
          newToken.add(new PhoneTag(test, "VNPT", "line")) ;
          holder.add(newToken) ;
          i += 1 ;
          continue ;
        }
      }

      if(norm.length() >= 7 && norm.length() < 15) {
        String string = VNPhoneNumberUtil.normalize(norm) ;
        if(string.length() == 7 || string.length() == 8) {
          token[i].add(new PhoneTag(string, "VNPT", "line")) ;
          holder.add(token[i]) ;
          continue ;
        }

        String areaCode = VNPhoneNumberUtil.getAreaCode(string) ;
        if(areaCode != null && string.length() < 12) {
          token[i].add(new PhoneTag(string, "VNPT", "line")) ;
          holder.add(token[i]) ;
          continue ;
        }
      }
      holder.add(token[i]) ;
    }
    return holder.toArray(new IToken[holder.size()]);
  }
}