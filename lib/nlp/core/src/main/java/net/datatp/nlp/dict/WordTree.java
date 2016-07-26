package net.datatp.nlp.dict;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.TokenTag;
import net.datatp.nlp.util.StringPool;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordTree {
  private Entry entry ;
  private Map<String, WordTree> children ;

  public WordTree() {
  }

  public Entry getEntry() { return this.entry ; }
  public void  setEntry(Entry entry) { this.entry = entry ; }

  public WordTree getChild(String word) { 
    if(children == null) return null ;
    return children.get(word) ;
  }

  public Entry add(String name, String[] word, int pos, TokenTag tag) {
    if(children == null) children = new HashMap<String, WordTree>() ;
    if(pos == word.length) {
      if(this.entry == null) {
        this.entry = new Entry(name, word) ;
      }
      this.entry.add(tag) ;
      return this.entry ;
    }
    WordTree child = children.get(word[pos]) ;
    if(child == null) {
      child = new WordTree() ;
      children.put(word[pos], child) ;
    }
    return child.add(name, word, pos + 1, tag) ;
  }

  public WordTree matches(IToken[] tokens, int pos) { 
    return matches(tokens, pos, tokens.length) ;
  }

  public WordTree matches(IToken[] tokens, int pos, int limit) { 
    if(children == null || pos == limit) return null ;
    IToken token = tokens[pos] ;
    WordTree foundTree = children.get(token.getNormalizeForm()) ;
    if(foundTree == null) return null ; 
    WordTree foundSubTree = foundTree.matches(tokens, pos + 1, limit) ;
    if(foundSubTree != null) return foundSubTree ;
    if(foundTree.getEntry() == null) return null ; 
    return foundTree ;
  }

  public WordTree find(IToken[] tokens, int pos, int limit) { 
    if(children == null || pos == limit) return null ;
    IToken token = tokens[pos] ;
    WordTree foundTree = children.get(token.getNormalizeForm()) ;
    if(foundTree == null) {
      return null ;
    }
    if(pos + 1 == limit) {
      if(foundTree.getEntry() == null) return null ;
      return foundTree ;
    }
    return foundTree.find(tokens, pos + 1, limit) ;
  }

  public WordTree find(String[] word, int pos, int limit) { 
    if(children == null || pos == limit) return null ;
    String token = word[pos] ;
    WordTree foundTree = children.get(token) ;
    if(foundTree == null) {
      return null ;
    }
    if(pos + 1 == limit) {
      if(foundTree.getEntry() == null) return null ;
      return foundTree ;
    }
    return foundTree.find(word, pos + 1, limit) ;
  }

  public void optimize(StringPool pool) {
    if(entry != null) entry.optimize(pool) ;
    if(children != null) {
      Iterator<WordTree> i = children.values().iterator() ;
      while(i.hasNext()) {
        WordTree wt = i.next() ;
        wt.optimize(pool) ;
      }
    }
  }

  public void collect(Map<String, Entry> holder) {
    if(entry != null) holder.put(entry.getName(), entry) ;
    if(children != null) {
      Iterator<WordTree> i = children.values().iterator() ;
      while(i.hasNext()) {
        WordTree wt = i.next() ;
        wt.collect(holder) ;
      }
    }
  }

  public void dump(PrintStream stream, String indent) {
    if(children == null) return ;
    String[] keys = children.keySet().toArray(new String[children.size()]) ;
    Arrays.sort(keys) ;	
    for(String wordUnit : keys) {
      WordTree child = children.get(wordUnit) ; 
      stream.print(indent) ;
      stream.print("+ ") ;
      stream.print(wordUnit) ;
      stream.print('[') ;
      if(child.getEntry() != null) {
        stream.print(child.getEntry().getName()) ;
      }
      stream.print(']') ;
      stream.println() ;
      child.dump(stream, indent + "  ") ;
    }
  }
}