package net.datatp.nlp.token.tag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTag extends TokenTag {
  final static public String TYPE   = "word" ;
  final static public String LETTER = "letter" ;

  final static public WordTag WLETTER = new WordTag(LETTER) ;
  final static public WordTag SEQ_LD  = new WordTag("letter+digit") ;
  final static public WordTag SEQ_LDD = new WordTag("letter+digit+dash") ;
  final static public WordTag VNWORD = new WordTag("vnword") ;

  private String otype ;

  public WordTag(String otype) {
    this.otype = otype ;
  }

  public String getOType() { return this.otype; }

  public boolean isTypeOf(String type) {
    if("word".equals(type)) return true ;
    return otype.equals(type) ;
  }

  public String getInfo() { return getOType() + ": {}" ; }
}
