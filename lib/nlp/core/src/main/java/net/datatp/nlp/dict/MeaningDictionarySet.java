package net.datatp.nlp.dict;

import java.util.ArrayList;
import java.util.List;

public class MeaningDictionarySet {
  private String otype;
  private List<MeaningDictionary> holder ;

  public MeaningDictionarySet(String otype, List<MeaningDictionary> dicts) {
    this.otype = otype;
    this.holder = dicts;
  }
  
  public Meaning[] find(String name, String[] type) {
    List<Meaning> mHolder  = new ArrayList<Meaning>() ;
    for(int i = 0; i < holder.size(); i++) {
      MeaningDictionary dict = holder.get(i);
      dict.find(mHolder, name, type);
    }
    return mHolder.toArray(new Meaning[holder.size()]) ;
  }
}
