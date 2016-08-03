package net.datatp.nlp.query.match;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.datatp.nlp.dict.EntityDictionary;
import net.datatp.nlp.dict.SynsetDictionary;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.TokenCollection;
import net.datatp.nlp.token.TokenException;
import net.datatp.nlp.token.WordTokenizer;
import net.datatp.nlp.token.analyzer.CommonTokenAnalyzer;
import net.datatp.nlp.token.analyzer.DateTokenAnalyzer;
import net.datatp.nlp.token.analyzer.EmailTokenAnalyzer;
import net.datatp.nlp.token.analyzer.PunctuationTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TimeTokenAnalyzer;
import net.datatp.nlp.token.analyzer.TokenAnalyzer;
import net.datatp.nlp.token.analyzer.USDTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNDTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNMobileTokenAnalyzer;
import net.datatp.nlp.vi.token.analyzer.VNPhoneTokenAnalyzer;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class RuleMatcherUnitTest {
  MatcherResourceFactory umFactory ;

  @Before
  public void setup() throws Exception {
    SynsetDictionary dict = new SynsetDictionary() ;
    dict.add("lienhe", new String[]{}, new String[]{"liên hệ"}) ;
    dict.add("person", new String[]{}, new String[]{"chúng tôi", "tôi", "anh"}) ;
    EntityDictionary entityDict = new EntityDictionary(EntityDictionary.DICT_RES) ;
    umFactory = new MatcherResourceFactory(dict, entityDict) ;
  }

  @Test
  public void testConstruction() throws Exception {
    assertRule(
        "/p word{word=liên hệ} .2. word{word=chúng tôi, tôi, anh}",
        "Hãy liên hệ chúng tôi vào những ngày trong tuần", 
        true
    );
    assertRule(
        "/p word{word=liên hệ} .0. synset{name=person}",
        "Hãy liên hệ chúng tôi vào những ngày trong tuần", 
        true
    );
    assertRule(
        "/p synset{name=lienhe} .2. synset{name=person}",
        "Hãy liên hệ với tôi vào những ngày trong tuần", 
        true
        );

    assertRule(
        "/p synset{name=lienhe} .10. entity{type = place, street}",
        "Hãy liên hệ với tôi tai pho Trương Định", 
        true
        );

    assertRule(
        "/p word{word=so} .2. digit",
        "match con so digit 123", 
        true
        );

    assertRule(
        "/p word{word=tien te} .2. currency{unit=vnd}",
        "match tien te 123 ngan tien viet", 
        true
        );
  }

  private void assertRule(String rule, String text, boolean expectMatch) throws Exception {
    RuleMatcher ruleMatcher = new RuleMatcher(umFactory, rule) ;
    TokenCollection tokenSet = createTokenSet(text) ;
    RuleMatch ruleMatch = ruleMatcher.matches(tokenSet) ;
    System.out.println("Text: " + text);
    System.out.println("  Rule: " + rule);
    if(ruleMatch != null) {
      System.out.println("  Match: " + ruleMatch);
    }
    if(expectMatch) {
      Assert.assertNotNull(ruleMatch) ;
    } else {
      Assert.assertNull(ruleMatch) ;
    }
  }

  private TokenCollection createTokenSet(String text) throws TokenException {
    TokenAnalyzer[] analyzer = {
        PunctuationTokenAnalyzer.INSTANCE, new CommonTokenAnalyzer(), 
        new DateTokenAnalyzer(), new TimeTokenAnalyzer(), 
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(),
        new VNPhoneTokenAnalyzer(), new VNMobileTokenAnalyzer(),
        new EmailTokenAnalyzer()	
    };
    IToken[] tokens = new WordTokenizer(text).allTokens() ;
    TokenCollection tokenSet = new TokenCollection(tokens) ;
    tokenSet.analyze(analyzer) ;
    return  tokenSet ;
  }
}