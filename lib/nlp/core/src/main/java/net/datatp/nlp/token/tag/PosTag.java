package net.datatp.nlp.token.tag;

import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PosTag extends WordTag {
  final static public String TYPE   = "postag" ;

  private String   name ;
  private String[] posTag ;

  public PosTag(String name, String ... posTag) {
    super(TYPE);
    this.name = name ;
    this.posTag = posTag ;
  }

  public boolean hasTag(String name) {
    for(String sel : posTag) {
      if(sel.equals(name)) return true ;
    }
    return false ;
  }

  public String[] getPosTag() { return posTag ; }
  public String   getTagValue() { return name ; }

  public void mergePosTag(String[] tag) {
    this.posTag = StringUtil.merge(posTag, tag) ;
  }

  public String getInfo() {
    StringBuilder b = new StringBuilder() ;
    b.append(getOType()).append(": {") ;
    String[] tag = posTag ;
    for(int i = 0; i < tag.length; i++) {
      if(i > 0) b.append(", ") ;
      b.append(tag[i]) ;
    }
    b.append("}") ;
    return b.toString() ;
  }
}