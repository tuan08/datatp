package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PersonChunker extends QueryChunker {
  public PersonChunker() throws Exception {
    String[] prefix = {
        "ông", "bà", "anh", "chị", "cô", "chú", "gì", "thím", "bác", "em", "cụ", "lão", "cậu"
    };

    String[] names = { "Nguyễn Minh Triết", "Trương Mỹ Hoa" };

    defineSynset("prefix", null, prefix);
    defineSynset("person", null, names);

    defineMatch(
        "/ synset{name=prefix} .2. synset{name=person}",
        "/ synset{name=prefix} .1. token{name=person}"
        );
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
  }
}