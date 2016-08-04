package net.datatp.nlp.query.match;

import net.datatp.nlp.dict.Meaning;
import net.datatp.nlp.dict.MeaningDictionary;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityUnitMatcher extends TreeWordMatcher {
  private String   name ;
  private String[] type ;

  public EntityUnitMatcher(MeaningDictionary dict, ParamHolder pholder, int allowNextMatchDistance) {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
    this.name = pholder.getFirstFieldValue("name") ;
    this.type = pholder.getFieldValue("type") ;
    Meaning[] meanings = dict.find(name, type) ;
    for(Meaning sel : meanings) {
      addWord(sel.getName()) ;
      addWord(sel.getVariant()) ;
    }
  }
}