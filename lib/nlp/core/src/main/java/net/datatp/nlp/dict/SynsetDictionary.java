package net.datatp.nlp.dict;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.datatp.nlp.util.StringPool;
import net.datatp.util.io.IOUtil;
import net.datatp.util.json.JSONReader;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SynsetDictionary {
  final static public String[] DICT_RES = {
      "classpath:nlp/lienhe.synset.json",
      "classpath:nlp/opinion/opinions.json",
      "classpath:nlp/opinion/nuance.json",
      "classpath:nlp/opinion/taxonomy.json",
      "classpath:nlp/opinion/marketingWords.json"
  } ;

  private List<Meaning> synsets = new ArrayList<Meaning>() ;

  public SynsetDictionary() {  }

  public SynsetDictionary(String[] res) throws Exception { 
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

  public void add(Meaning meaning) {
    if(!"synset".equals(meaning.getOType())) {
      throw new RuntimeException(meaning.getName() + " is not a synset") ;
    }
    synsets.add(meaning) ;
  }

  public void add(String name, String[] type, String[] variant) {
    Meaning meaning = new Meaning() ;
    meaning.setOType("synset") ;
    meaning.setName(name) ;
    meaning.setType(type) ;
    meaning.setVariant(variant) ;
    add(meaning) ;
  }

  public Meaning[] find(String name, String[] type) {
    List<Meaning> holder  = new ArrayList<Meaning>() ;
    for(int i = 0; i < synsets.size(); i++) {
      Meaning synset = synsets.get(i) ;
      if(name != null) {
        if(!name.equals(synset.getName())) continue ;
      }
      if(type != null) {
        String[] synsetType = synset.getType() ;
        if(!StringUtil.isIn(type, synsetType))  continue ;
      }
      holder.add(synset) ;
    }
    return holder.toArray(new Meaning[holder.size()]) ;
  }

  public void optimize(StringPool pool) {
    for(int i = 0; i < synsets.size(); i++) {
      synsets.get(i).optimize(pool) ;
    }
  }
}