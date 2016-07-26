package net.datatp.nlp.query.match;

import net.datatp.nlp.dict.Meaning;
import net.datatp.nlp.dict.SynsetDictionary;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SynsetUnitMatcher extends TreeWordMatcher {
  private String   name ;
  private String[] type ;

  public SynsetUnitMatcher(SynsetDictionary dict, ParamHolder pholder, int allowNextMatchDistance) {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
    this.name = pholder.getFirstFieldValue("name") ;
    this.type = pholder.getFieldValue("type") ;
    Meaning[] synset = dict.find(name, type) ;
    for(Meaning selSynset : synset) {
      String[] variant = selSynset.getVariant() ;
      addWord(variant) ;
    }
  }
}