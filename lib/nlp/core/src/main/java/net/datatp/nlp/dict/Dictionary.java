package net.datatp.nlp.dict;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.datatp.nlp.token.tag.MeaningTag;
import net.datatp.nlp.token.tag.PosTag;
import net.datatp.nlp.util.StringPool;
import net.datatp.util.io.IOUtil;
import net.datatp.util.json.JSONReader;
import net.datatp.util.text.StringExpMatcher;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Dictionary {
  final static public String[] DICT_RES = {
    "classpath:nlp/vn.lexicon.json",
    "classpath:nlp/vn.lexicon-uncategorized.json",
    "classpath:nlp/entity/vn.place.json"
  } ;

  private WordTree root = new WordTree() ;
  private Map<String, Entry>  entries = new HashMap<String, Entry>() ;

  public Dictionary() { }

  public Dictionary(String[] res) throws Exception { 
    for(String sel : res) {
      InputStream is = IOUtil.loadRes(sel) ;
      JSONReader reader = new JSONReader(is) ;
      Meaning meaning = null ;
      while((meaning = reader.read(Meaning.class)) != null) {
        add(meaning) ;
      }
    }
    optimize(new StringPool()) ;
  }

  public  WordTree getWordTree() { return this.root ; }

  public Entry getEntry(String name) { return entries.get(name) ; }

  public Entry[] find(String nameExp) { 
    List<Entry> holder = new ArrayList<Entry>() ;
    StringExpMatcher matcher = new StringExpMatcher(nameExp) ;
    Iterator<Entry> i = entries.values().iterator() ;
    while(i.hasNext()) {
      Entry entry = i.next() ;
      if(matcher.matches(entry.getName())) holder.add(entry) ;
    }
    return holder.toArray(new Entry[holder.size()]) ; 
  }

  public void add(Meaning meaning) {
    String[] array = StringUtil.merge(meaning.getVariant(), meaning.getName()) ;
    String otype = meaning.getOType() ;
    String[] posTag = meaning.getStringArray("postag") ;
    if("place".equals(otype) || "person".equals(otype)) {
      posTag = StringUtil.merge(posTag, "pos:Np") ;
    }
    if(posTag == null) posTag = new String[] {"pos:X"} ;

    MeaningTag mtag = new MeaningTag(meaning) ;

    for(String sel : array) {
      String nName = sel.toLowerCase() ;
      PosTag ptag = new PosTag(nName, posTag) ;
      String[] word = nName.split(" ") ;
      WordTree tree = root.find(word, 0, word.length) ;
      if(tree != null) {
        Entry entry = tree.getEntry() ;
        PosTag exist = entry.getFirstTagType(PosTag.class) ;
        if(exist != null) {
          exist.mergePosTag(ptag.getPosTag()) ;
        } else {
          entry.add(ptag) ;
        }
      } else {
        root.add(nName, word, 0, ptag) ;
      }
      root.add(nName, word, 0, mtag) ;
    }
  }

  public void optimize(StringPool pool) {
    root.optimize(pool) ;
    this.entries.clear() ;
    root.collect(this.entries) ;
  }
}