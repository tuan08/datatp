package net.datatp.nlp.query.match;

import net.datatp.nlp.dict.MeaningDictionary;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class MatcherResourceFactory {
  private MeaningDictionary synsetDictionary ;
  private MeaningDictionary entityDictionary ;


  public MatcherResourceFactory(MeaningDictionary synsetDict, MeaningDictionary entityDict) {
    this.synsetDictionary = synsetDict ;
    this.entityDictionary = entityDict ;
  }

  public MeaningDictionary getSynsetDictionary() { return this.synsetDictionary ; }

  public MeaningDictionary getEntityDictionary() { return this.entityDictionary ; }

  public UnitMatcher create(String exp, String distanceString) throws Exception {
    int allowNextMatchDistance = 100000 ;
    if(distanceString != null && distanceString.length() > 0) {
      allowNextMatchDistance = Integer.parseInt(distanceString) ;
    }
    exp = exp.trim() ;
    ParamHolder pholder = new ParamHolder(exp) ;
    if("synset".equals(pholder.getName())) {
      return new SynsetUnitMatcher(synsetDictionary, pholder, allowNextMatchDistance) ;
    } else if("entity".equals(pholder.getName())) {
      return new EntityUnitMatcher(entityDictionary, pholder, allowNextMatchDistance) ;
    } else if("word".equals(pholder.getName())) {
      return new WordUnitMatcher(pholder, allowNextMatchDistance) ;
    } else if("regex".equals(pholder.getName())) {
      String regex = exp.substring("regex{".length(), exp.length() - 1) ;
      return new RegexUnitMatcher(regex, allowNextMatchDistance) ;
    } else if("digit".equals(pholder.getName())) {
      return new DigitUnitMatcher(pholder, allowNextMatchDistance) ;
    } else if("number".equals(pholder.getName())) {
      return new NumberUnitMatcher(pholder, allowNextMatchDistance) ;
    } else if("currency".equals(pholder.getName())) {
      return new CurrencyUnitMatcher(pholder, allowNextMatchDistance) ;
    }
    throw new Exception("Unknown matcher " + pholder.getName()) ;
  }
}
