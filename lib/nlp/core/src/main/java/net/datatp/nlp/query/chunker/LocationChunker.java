package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class LocationChunker extends QueryChunker {
  public LocationChunker() throws Exception {
    String[] street = { 
        "phố", "đường", "ngõ", "ngách", "hẻm", "street"
    };

    defineSynset("street", null, street);
    defineMatches("/ synset{name=street} .3. entity{type=place}");
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {

  }
}