package net.datatp.nlp.dict;

import net.datatp.nlp.token.tag.TokenTag;
import net.datatp.nlp.util.StringPool;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Entry {
  private String[] word ;
  private String   name ;
  private TokenTag[] tag ;

  public Entry(String name, String[] word) {
    this.name = name ;
    this.word = word ;
  }

  public String[] getWord() { return word; }
  public void     setWord(String[] word) { this.word = word; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public void add(TokenTag tag) {
    if(tag == null) return ;
    if(this.tag == null) {
      this.tag = new TokenTag[] { tag } ;
    } else {
      TokenTag[] tmp = new TokenTag[this.tag.length + 1] ;
      System.arraycopy(this.tag, 0, tmp, 0, this.tag.length) ;
      tmp[this.tag.length] = tag ;
      this.tag = tmp ;
    }
  }
  public TokenTag[] getTag() { return tag; }
  public void setTag(TokenTag[] tag) { this.tag = tag; } 

  public <T extends TokenTag> T getFirstTagType(Class<T> type) {
    if(type == null || tag == null) return null ;
    for(TokenTag sel : tag) if(type.isInstance(sel)) return type.cast(sel) ;
    return null ;
  }

  public void optimize(StringPool pool) {
    word = pool.getString(word) ;
    name = pool.getString(name) ;
    if(tag != null) {
      for(int i = 0; i < tag.length; i++) {
        tag[i].optimize(pool) ;
      }
    }
  }

  public void dump() {
    System.out.println("name: " + name) ;
    if(this.tag != null) {
      for(TokenTag sel : tag) {
        System.out.println("  tag: " + sel.getInfo()) ;
      }
    }
  }
}