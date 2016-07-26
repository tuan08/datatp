package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.query.match.TreeWordMatchers;
import net.datatp.nlp.query.match.UnitMatch;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.EntityTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class HintEntityChunker extends QueryChunker {
  private TreeWordMatchers hintMatchers = new TreeWordMatchers() ;

  public HintEntityChunker(MatcherResourceFactory mrFactory) throws Exception {
    super(mrFactory) ;
    String[] perPrefix = {
        "ông", "bà", "anh", "chị", "cô", "chú", "gì", "thím", "bác", "em", "cụ", "lão", "cậu",
        "mr", "mr.", "miss", "miss."
    };
    hintMatchers.add("person:prefix", perPrefix) ;

    String[] placePrefix = { 
        "phố", "đường", "ngõ", "ngách", "hẻm", "tp", "thành phố", "tỉnh", "huyện", "xã", 
        "thị trấn", "thị xã"
    };
    hintMatchers.add("place:prefix", placePrefix) ;
    defineEntity("Trần Hưng Đạo", new String[] { "person" }, null) ;

    defineMatch("ent:person", "/ entity{type=person}");
    defineMatch("ent:place" , "/ entity{type=place}");
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
    IToken set = createChunking(token, from, to) ;
    int hintFrom = from - 4 ;
    if(hintFrom < 0) hintFrom = 0 ;
    int hintTo = to + 4 ;
    if(hintTo > token.length) hintTo = token.length ;

    UnitMatch[] hintMatch = hintMatchers.matches(token, hintFrom, hintTo) ;
    double personPoint = 0.0 ;
    double placePoint = 0.0 ;
    if(hintMatch != null) {
      for(UnitMatch selHintMatch : hintMatch) {
        String name = selHintMatch.getUnitMatcher().getName() ;
        if("person:prefix".equals(name)) personPoint += 1;
        else if("place:prefix".equals(name)) placePoint += 1;
      }
    }
    if(personPoint == placePoint) {
      String ruleName = rmatcher.getName() ;
      if("ent:person".equals(ruleName))  personPoint += 1;
      else if("ent:place".equals(ruleName)) placePoint += 1 ;
    }

    //System.out.println("person = " + personPoint + ", place = " + placePoint);
    if(placePoint > personPoint) {
      set.add(new EntityTag("location", null)) ;
    } else {
      set.add(new EntityTag("person", null)) ;
    }
    holder.add(set) ;
  }
}