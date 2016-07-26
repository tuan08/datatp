package net.datatp.nlp;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import net.datatp.nlp.dict.Dictionary;
import net.datatp.nlp.dict.EntityDictionary;
import net.datatp.nlp.dict.SynsetDictionary;
import net.datatp.nlp.dict.WordTreeDictionary;
import net.datatp.util.io.IOUtil;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class NLPResource {
  static NLPResource instance = null ;
  static int instanceCount = 0 ;

  private Map<String, Object> resCache = new HashMap<String, Object>() ;

  public NLPResource() {
    if(instanceCount > 0) {
      new Exception("WARNING: create more than one NLPResource instance").printStackTrace() ;
    }
    instanceCount++ ;
  }

  public <T> T getObject(String path) {
    try {
      String key = path ;
      int colon = key.indexOf(':') ;
      if(colon > 0) key =  key.substring(colon + 1) ;
      Object model = resCache.get(key) ;
      if(model == null) {
        ObjectInputStream s = new ObjectInputStream(IOUtil.loadRes(path));
        model =  s.readObject();
        s.close();
        resCache.put(key, model) ;
      }
      return (T) model ;
    } catch(Exception ex) {
      throw new RuntimeException(ex) ;
    }
  }

  public <T> T getDictionary(String[] res) throws Exception {
    String key = StringUtil.joinStringArray(res, ",");
    Dictionary dict = (Dictionary) resCache.get(key) ;
    if(dict == null) {
      dict = new Dictionary(res);
      resCache.put(key, dict) ;
    }
    return (T) dict ;
  }

  public <T> T getWordTreeDictionary(String[] res) throws Exception {
    String key = StringUtil.joinStringArray(res, ",");
    WordTreeDictionary dict = (WordTreeDictionary) resCache.get(key) ;
    if(dict == null) {
      dict = new WordTreeDictionary(res);
      resCache.put(key, dict) ;
    }
    return (T) dict ;
  }

  public <T> T getSynsetDictionary(String[] res) throws Exception {
    String key = StringUtil.joinStringArray(res, ",");
    SynsetDictionary dict = (SynsetDictionary) resCache.get(key) ;
    if(dict == null) {
      dict = new SynsetDictionary(res);
      resCache.put(key, dict) ;
    }
    return (T) dict ;
  }

  public <T> T getEntityDictionary(String[] res) throws Exception {
    String key = StringUtil.joinStringArray(res, ",");
    EntityDictionary dict = (EntityDictionary) resCache.get(key) ;
    if(dict == null) {
      dict = new EntityDictionary(res);
      resCache.put(key, dict) ;
    }
    return (T) dict ;
  }

  static public NLPResource getInstance() {
    if(instance == null) instance = new NLPResource() ;
    return instance ;
  }
}