package net.datatp.nlp.token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.datatp.nlp.token.tag.MeaningTag;
import net.datatp.nlp.token.tag.TokenTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class IToken {
  private List<TokenTag> tag ;

  abstract public String[] getWord() ;
  abstract public String   getOriginalForm() ;
  abstract public String   getNormalizeForm() ;
  abstract public char[]   getNormalizeFormBuf() ;

  public void reset(String text) {
    throw new RuntimeException("Method is not supported") ;
  }

  public boolean hasTag() { return tag != null && tag.size() > 0 ; }
  public List<TokenTag> getTag() { return this.tag ; }
  public void  setTag(List<TokenTag> tag) { this.tag = tag ; }

  public boolean hasTagType(String type) {
    if(tag == null) return false ; 
    for(int i = 0; i < tag.size(); i++) {
      if(tag.get(i).isTypeOf(type)) return true ;
    }
    return false ;
  }

  public boolean hasTagType(String[] type) {
    if(tag == null) return false ; 
    for(TokenTag sel : tag) {
      for(String selType : type) {
        if(sel.isTypeOf(selType)) return true ;
      }
    }
    return false ;
  }

  public void add(TokenTag tag) {
    if(tag == null) return ;
    if(this.tag == null) this.tag = new ArrayList<TokenTag>(3) ;
    this.tag.add(tag) ;
  }

  public void add(TokenTag[] tag) {
    if(tag == null) return ;
    if(this.tag == null) this.tag = new ArrayList<TokenTag>(5) ;
    for(int i = 0; i < tag.length; i++) this.tag.add(tag[i]) ;
  }

  public TokenTag getFirstTagType(String type) {
    if(tag == null) return null ; 
    for(TokenTag sel : tag) if(sel.isTypeOf(type)) return sel ;
    return null ;
  }

  public <T extends TokenTag> T getFirstTagType(Class<T> type) {
    if(type == null || tag == null) return null ;
    for(TokenTag sel : tag) if(type.isInstance(sel)) return type.cast(sel) ;
    return null ;
  }

  public <T extends TokenTag> HashSet<String> getMeaningTypes() {
    if(tag == null) return null ;
    HashSet<String> set = null ;
    for(TokenTag sel : tag) { 
      if(sel instanceof MeaningTag) {
        if(set == null) set = new HashSet<String>() ;
        set.add(((MeaningTag)sel).getOType()) ;
      }
    }
    return set ;
  }

  public <T extends TokenTag> void removeTagType(Class<T> type) {
    if(type == null || tag == null) return  ;
    Iterator<TokenTag> i = tag.iterator() ;
    while(i.hasNext()) {
      if(type.isInstance(i.next())) {
        i.remove() ;
      }
    }
  }

  public <T extends TokenTag> void removeTagType(Class<T>[] type) {
    if(type == null || tag == null) return  ;
    Iterator<TokenTag> i = tag.iterator() ;
    while(i.hasNext()) {
      for(Class<T> sel : type) {
        if(sel.isInstance(i.next())) {
          i.remove() ;
          break ;
        }
      }
    }
  }

  public <T extends TokenTag> List<T> getTagByType(String type) {
    if(tag == null) return null ; 
    List<T> holder = new ArrayList<T>(tag.size()) ;
    for(TokenTag sel : tag) {
      if(sel.isTypeOf(type)) holder.add((T)sel) ;
    }
    return holder ;
  }

  public <T extends TokenTag> List<T> getTagByType(String[] type) {
    if(tag == null) return null ; 
    List<T> holder = new ArrayList<T>(tag.size()) ;
    for(TokenTag sel : tag) {
      for(String selType : type) {
        if(type.equals(sel.isTypeOf(selType))) {
          holder.add((T)sel) ;
          break ;
        }
      }
    }
    return holder ;
  }

  public void clearTag() { tag = null ; }
}