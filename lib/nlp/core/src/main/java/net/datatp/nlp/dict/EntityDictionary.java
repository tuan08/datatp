package net.datatp.nlp.dict;

import java.io.InputStream;

import net.datatp.util.dataformat.DataReader;
import net.datatp.util.io.IOUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityDictionary extends MeaningDictionary {
  final static public String[] DICT_RES = {
      "classpath:nlp/entity/vn.place.json",
      "classpath:nlp/entity/vn.person.json",
      "classpath:nlp/entity/mobile.product.json"
  } ;

  public EntityDictionary() { super("entity"); }

  public EntityDictionary(String[] res) throws Exception {
    super("entity");
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