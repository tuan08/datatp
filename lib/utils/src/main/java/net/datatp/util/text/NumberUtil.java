package net.datatp.util.text;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class NumberUtil {
  final static public java.lang.Double parseRealNumber(char[] buf) {
    if(buf == null  || buf.length == 0 || !Character.isDigit(buf[0])) return null ;
    int fractionPosition = -1 ;
    int dotCounter = 0, commaCounter = 0;
    int digitCount = 0 ;
    for(int i = 0; i < buf.length; i++) {
      char c = buf[i] ;
      if(c == '.') {
        fractionPosition = i ; dotCounter++ ;
        continue ;
      } else if(c == ',') {
        fractionPosition = i ; commaCounter++ ;
        continue ;
      } else if(c >= '0' && c <= '9') {
        digitCount++ ; continue ;
      }
      return null ;
    }
    if(digitCount == 0) return null ;
    if(dotCounter > 1 && commaCounter > 1) return null ;
    String string = new String(buf) ;
    if(fractionPosition < 0) return java.lang.Double.parseDouble(string) ;
    if(string.endsWith("000")) fractionPosition = -1 ;
    if((commaCounter > 0 && dotCounter == 0) || (commaCounter == 0 && dotCounter > 0)) {
      if(string.length() - fractionPosition < 5) {
        if(string.endsWith(".00") && string.length() < 5) fractionPosition = -1 ;
        else if(string.endsWith(",00") && string.length() < 5) fractionPosition = -1 ;
        else if(string.length() - fractionPosition == 4) fractionPosition = -1 ;
      }
    }
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < buf.length; i++) {
      char c = buf[i] ;
      if(Character.isDigit(c)) {
        b.append(c) ;
      } else {
        if(i == fractionPosition) b.append(".") ;
      }
    }
    if(b.length() == 0) return null ;
    try {
      return java.lang.Double.parseDouble(b.toString()) ;
    } catch(Throwable t) {
      System.out.println("Cannot parse " + new String(buf));
      t.printStackTrace() ;
      return null ;
    }
  }

  static public boolean isDigits(String s) {
    if(s == null || s.length() == 0) return false ;
    for(char c : s.toCharArray()) {
      if(c >= '0' && c <= '9') continue ;
      return false ;
    }
    return true ;
  }
}
