package net.datatp.nlp.dict;

import java.io.InputStream;

import net.datatp.util.dataformat.DataReader;
import net.datatp.util.io.IOUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SynsetDictionary extends MeaningDictionary {
  final static public String[] DICT_RES = {
      "classpath:nlp/lienhe.synset.json",
      "classpath:nlp/opinion/opinions.json",
      "classpath:nlp/opinion/nuance.json",
      "classpath:nlp/opinion/taxonomy.json",
      "classpath:nlp/opinion/marketingWords.json"
  } ;

  public SynsetDictionary() {  super("synset") ;}

  public SynsetDictionary(String[] res) throws Exception { 
    super("synset");
    for(String sel : res) {
      InputStream is = IOUtil.loadRes(sel) ;
      DataReader reader = new DataReader(is) ;
      Meaning meaning = null ;
      while((meaning = reader.read(Meaning.class)) != null) {
        add(meaning) ;
      }
    }
  }
}