package net.datatp.nlp.token.analyzer;

import java.util.List;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.tag.TimeTag;
import net.datatp.nlp.token.tag.WordTag;
import net.datatp.nlp.util.CharacterSet;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class TimeTokenAnalyzer extends TokenAnalyzer {
  public IToken[] analyze(IToken[] token) throws TokenException {
    for(int i = 0; i < token.length; i++) {
      if(token[i].hasTagType(WordTag.LETTER)) ;
      analyze(token, i) ;
    }
    return token ;
  }

  public void analyze(IToken[] tokens, int pos) {
    if(tokens[pos].hasTagType(WordTag.LETTER)) return  ;
    char[] buf = tokens[pos].getNormalizeFormBuf() ;
    if(!CharacterSet.isDigit(buf[0])) return  ;
    IToken token = tokens[pos] ;
    String norm = token.getNormalizeForm() ;
    if(norm.endsWith("am") || norm.endsWith("pm")) {
      String timeToken = norm.substring(0, norm.length() - 2) ;
      String apm = norm.substring(norm.length() - 2) ;
      setTimeTag(token, timeToken, apm) ;
      return ;
    }

    String apm = null ;
    if(pos + 1 < tokens.length) {
      String nextTokenNorm = tokens[pos + 1].getNormalizeForm() ;
      if(nextTokenNorm.endsWith("am") || nextTokenNorm.endsWith("pm")) apm = nextTokenNorm ;
    }
    setTimeTag(token, norm, apm) ;
  }

  private void setTimeTag(IToken token, String timeToken, String pm) {
    int hourShift = 0 ;
    if("pm".equals(pm)) hourShift = 12 ;
    char[] buf = timeToken.toCharArray() ;
    int sepCount = 0;
    int digitCount = 0 ;
    for(int i = 0; i < buf.length; i++) {
      if(buf[i] == ':') sepCount++ ;
      if(CharacterSet.isDigit(buf[i])) digitCount++ ;
    }
    if(digitCount + sepCount !=  buf.length) return ;
    if(sepCount < 1 || sepCount > 2) return ;
    List<String> tmp = StringUtil.split(buf, ':') ;
    if(tmp.size() < 2) return ; // case xx:
    short hh = parseShort(tmp.get(0), (short)-1) ;
    hh += hourShift ;
    if(hh < 0 || hh > 24) return  ;
    short mm = parseShort(tmp.get(1), (short)-1) ;
    if(mm < 0 || mm > 59) return  ;
    short ss = 0 ;
    if(tmp.size() > 2) {
      ss = parseShort(tmp.get(2), (short)-1) ;
      if(ss < 0 || ss > 59) return  ;
    }
    TimeTag ttag = new TimeTag(hh + ":" + mm + ":" + ss) ;
    token.add(ttag) ;
  }

  private short parseShort(String value, short defaultValue) {
    try {
      return Short.parseShort(value) ;
    } catch(NumberFormatException ex) {
      return defaultValue ;
    }
  }
}