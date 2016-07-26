package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.DateTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class DateChunker extends QueryChunker {
  public DateChunker() throws Exception {
    String[] month = {
        "tháng một", "tháng 1", "tháng giêng", "january", "jan",
        "tháng hai", "tháng 2", "february", "feb",
        "tháng ba",  "tháng 3", "march", "mar",
        "tháng tư",  "tháng 4", "april", "apr",
        "tháng năm",  "tháng 5", "may",
        "tháng sáu",  "tháng 6", "june", "jun",
        "tháng bảy",  "tháng 7", "jully", "jul",
        "tháng tám",  "tháng 7", "august", "aug",
        "tháng chín",  "tháng 9", "september", "sep",
        "tháng mười",  "tháng 10", "october", "oct",
        "tháng mười một",  "tháng 11", "november", "nov",
        "tháng mười hai",  "tháng 12", "december", "dec"
    } ;

    String[] day = {
        "thứ hai", "thứ 2", "monday", "mon", "mo",
        "thứ ba", "thứ 3", "tuesday", "tue", "tu",
        "thứ tư", "thứ 4", "wednesday", "wed", "we",
        "thứ năm", "thứ 5", "thursday", "thu", "th",
        "thứ sáu", "thứ 6", "friday", "fri", "fr",
        "thứ bảy", "thứ bẩy", "thứ 7", "satursday", "sat", "sa",
        "chủ nhật", "chủ nhựt", "sunday", "sun", "su"
    } ;
    defineSynset("ck:date:month", null, month) ;
    defineSynset("ck:date:day", null, day) ;
    defineMatches(
        //slash and frequency
        "/ regex{\\d{1,2}[/.\\-]\\d{1,2}[/.\\-]\\d{1,4}}",
        // Vietnamese format 
        // + full format without weekday
        "/ word{word=ngày} .1. regex{\\d{1,2}} .2. synset{name=ck:date:month} .2. regex{\\d{2,4}}",
        // + full format with weekday
        "/ synset{name=ck:date:day} .1. regex{\\d{1,2}} .2. synset{name=ck:date:month} .2. regex{\\d{2,4}}",

        // English date format
        // + full format start with weekday
        "/ synset{name=ck:date:day} .2. synset{name=ck:date:month} .1. regex{\\d{1,2}} .1. regex{\\d{2,4}}" ,
        // + short format without weekday began start with month 
        "/ synset{name=ck:date:month} .1. regex{\\d{1,2}} .1. regex{\\d{2,4}}",

        // General format
        // + short format, only contain month and day/year
        "/ synset{name=ck:date:month} .1. regex{\\d{1,4}}",
        // + short format without weekday
        "/ regex{\\d{1,2}} .2. synset{name=ck:date:month} .2. regex{\\d{1,4}}"
        ) ;
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
    IToken set = createChunking(token, from, to) ;
    DateTag tag = new DateTag("???") ;
    set.add(tag) ;
    holder.add(set) ;
  }
}