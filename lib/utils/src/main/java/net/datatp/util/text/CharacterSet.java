package net.datatp.util.text;

public class CharacterSet {
  static public boolean isWordCharacter(char[] buf, int pos) {
    char c =  buf[pos] ;
    if(c >= 'A' && c <= 'Z') return true ;
    if(c >= 'a' && c <= 'z') return true ;
    if(c >= '0' && c <= '9') return true ;
    if(Character.isLetter(c)) return true ;
    if(c == '$') return true ;
    if(c == '%') return true ;
    
    if(pos == buf.length - 1) return false ;
    if(c == '.' || c == '-' || c == '_' || c == '/' || c == '@' || c == ':') {
      if(pos + 1 == buf.length) return false ;
      if(isAlphaDigit(buf[pos + 1])) return true ;
    }
    if(c == ',') {
      if(pos + 1 == buf.length) return false ;
      if(Character.isDigit(buf[pos + 1])) return true ;
    }
    if(c == '₫') return true;//for vnd type
    return false  ;
  }
  
  static public boolean isBlank(char c) {
    if(c == ' ' || c == '\t' || c == 160) return true ;
    return false  ;
  }
  
  static public boolean isNewLine(char c) {
    if(c == '\n' || c == '\f' || c == '\r' || c == 13) return true ;
    return false  ;
  }
  
  public final static boolean isPunctuation(char c) {
    //apostrophe
    if(c == '\'' || c == '’') return true ;
    //Brackets
    if(c == '(' || c == ')' || c == '[' || c == ']' || 
       c == '{' || c == '}' || c == '<' || c == '>') return true ;
    //colon
    if(c == ':') return true ;
    //comma
    if(c == ',') return true ;
    //dashes
    if( c == '‒' || c == '–' || c =='—' || c == '―') return true ;
    //exclamation mark  
    if(c == '!' ) return true ;
    //full stop (period)  
    if(c == '.') return true;
    //guillemets  
    if(c == '«' || c == '»' ) return true ;
    //hyphen  
    if( c == '-' || c == '‐' ) return true ;
    //question mark   
    if(c == '?') return true ;
    //quotation marks   
    if(c == '"' || c == '‘' || c ==  '’' || c == '“' || c == '”') return true ;
    //semicolon   
    if(c == ';') return true ;
    //slash/stroke  
    if(c ==  '/' ) return true ;
    //solidus   
    if(c == '⁄') return true ;
    return false ;
  }
  
  public final static boolean isNumberOrNumberSeparator(char c) {
    if(c >= '0' && c <= '9') return true ;
    if(c == '.' || c == ',' || c == '-' || c == '/' || c == ':') return true ;
    return false ;
  }
  
  final static public boolean isEndSentence(char c) {
    if(c == '.' || c == '!' || c == '?' || c == ';') return true ;
    return false ;
  }
  
  final static public boolean isEndPhrase(char c) {
    if(c == '.' || c == '!' || c == '?') return true ;
    if(c == ',' || c == ';' || c == ':') return true ;
    //hyphen  
    if( c == '-' || c == '‐' ) return true ;
    //quotation marks   
    if(c == '"' || c == '‘' || c ==  '’' || c == '“' || c == '”') return true ;
    //guillemets  
    if(c == '«' || c == '»' ) return true ;
    return false ;
  }
  
  final static public boolean isBracket(char c) {
    if(c == '(' || c == ')' || c == '[' || c == ']' || 
       c == '{' || c == '}' || c == '<' || c == '>') return true ;
    return false ;
  }
  
  final static public boolean isQuotationMark(char c) {
    //quotation marks   
    if(c == '"' || c == '‘' || c ==  '’' || c == '“' || c == '”') return true ;
    //guillemets  
    if(c == '«' || c == '»' ) return true ;
    return false ;
  }
  
  static public boolean isAlphaDigit(char c) {
    if(c >= 'A' && c <= 'Z') return true ;
    if(c >= 'a' && c <= 'z') return true ;
    if(c >= '0' && c <= '9') return true ;
    if(Character.isLetter(c)) return true ;
    return false  ;
  }
  
  static public boolean isDigit(char c) {
  	if(c >= '0' && c <= '9') return true ;
  	return false ;
  }
}