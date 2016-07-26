package net.datatp.nlp.query.match;

import java.util.HashMap;
import java.util.Map;

import net.datatp.nlp.NLPResource;
import net.datatp.nlp.dict.EntityDictionary;
import net.datatp.nlp.dict.SynsetDictionary;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class MatcherResourceFactory {
  private SynsetDictionary synsetDictionary ;
  private EntityDictionary entityDictionary ;
  private Map<String, UnitMatcher> cacheMatchers = new HashMap<String, UnitMatcher>() ;

  public MatcherResourceFactory() throws Exception {
    this.synsetDictionary = NLPResource.getInstance().getSynsetDictionary(SynsetDictionary.DICT_RES) ;
    this.entityDictionary = NLPResource.getInstance().getEntityDictionary(EntityDictionary.DICT_RES) ;
  }

  public MatcherResourceFactory(SynsetDictionary synsetDictionary, 
      EntityDictionary entityDictionary) {
    this.synsetDictionary = synsetDictionary ;
    this.entityDictionary = entityDictionary ;
  }

  public SynsetDictionary getSynsetDictionary() { return this.synsetDictionary ; }

  public EntityDictionary getEntityDictionary() { return this.entityDictionary ; }

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
