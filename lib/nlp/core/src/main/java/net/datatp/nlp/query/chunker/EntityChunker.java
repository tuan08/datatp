package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityChunker extends QueryChunker {
  public EntityChunker() throws Exception {
    String[] perPrefix = {
        "ông", "bà", "anh", "chị", "cô", "chú", "gì", "thím", "bác", "em", "cụ", "lão", "cậu",
        "mr", "mr.", "miss", "miss."
    };
    String[] placePrefix = { 
        "phố", "đường", "ngõ", "ngách", "hẻm", "tp", "thành phố", "tỉnh", "huyện", "xã", 
        "thị trấn", "thị xã"
    };

    defineSynset("ent:per:prefix", null, perPrefix);
    defineSynset("ent:place:prefix", null, placePrefix);

    defineMatch("ent:person", "/ synset{name=ent:per:prefix} .2. entity{type=person}");

    defineMatch("ent:place", "/ synset{name=ent:place:prefix} .2. entity{type=place}");

    defineMatch("ent:person", "/ entity{type=person}");

    defineMatch("ent:place", "/ entity{type=place}");
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
  }
}