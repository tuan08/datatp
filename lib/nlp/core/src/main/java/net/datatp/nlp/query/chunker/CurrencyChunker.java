package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.query.match.MatcherResourceFactory;
import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.CurrencyTag;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class CurrencyChunker extends QueryChunker {
  public CurrencyChunker(MatcherResourceFactory mrFactory) throws Exception {
    super(mrFactory) ;

    String[] currUnit = {
        "đồng", "dong", "vnđ", "vnd", "đ",
        "đô", "đô la", "do la", "dollard", "usd", "us$", "$", 
        "euro", "eur", "€"
    };

    String[] wordNum = {
        "một", "hai", "ba", "bốn", "năm", "sáu", "bẩy", "tám", "chín", "mười",
        "chục", "trăm", "tỷ"
    };

    String[] wordNumUnit = {
        "chục", "mươi", "lăm", "nhăm", 
        "trăm", 
        "ngàn", "ngan", 
        "triệu", "trieu", "tr",
        "tỷ", "ty"
    };

    String currExp = "(\\d{1,3}[.,\\-]?)+";
    String allUnit = StringUtil.joinStringArray(StringUtil.merge(wordNumUnit, currUnit), "|");

    defineSynset("ck:curr:num", null, wordNum);
    defineSynset("ck:curr:num:unit", null, wordNumUnit);
    defineSynset("ck:curr:unit", null, currUnit);
    defineMatches(
        // formats start with digit, followed by units
        "/ number .1. synset{name=ck:curr:num:unit} .0. synset{name=ck:curr:num:unit} .0. synset{name=ck:curr:unit}",
        "/ number .1. synset{name=ck:curr:num:unit} .0. synset{name=ck:curr:num:unit}",
        "/ number .1. synset{name=ck:curr:num:unit} .0. synset{name=ck:curr:unit}",
        "/ number .1. synset{name=ck:curr:num:unit} .0. digit",

        // formats followed by an unit in bracket
        "/ regex{" + currExp + "} .0. regex{\\(} .0. synset{name=ck:curr:unit} .0. regex{\\)}",

        // formats followed by an unit
        "/ regex{" + currExp + "} .0. regex{(" + allUnit + ")}",

        // formats contain an unit in the middle of string
        "/ regex{\\d{1,10}?(" + allUnit + ")\\d{1,5}?}",
        "/ regex{\\d+.*(" + allUnit + ").*}",

        // full formats start with word numbers
        "/ synset{name=ck:curr:num} .0. synset{name=ck:curr:num:unit} .0. synset{name=ck:curr:unit}",
        "/ synset{name=ck:curr:num} .0. synset{name=ck:curr:unit}"
        ) ;
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
    IToken set = this.createChunking(token, from, to) ;
    double value = getValue(set);
    CurrencyTag tag = new CurrencyTag(value, "unit");
    set.add(tag);
    holder.add(set) ;
  }

  private double getValue(IToken token){
    return 0;
  }
}