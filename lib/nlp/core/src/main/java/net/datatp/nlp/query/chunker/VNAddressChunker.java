package net.datatp.nlp.query.chunker;

import java.util.List;

import net.datatp.nlp.NLP;
import net.datatp.nlp.query.match.RuleMatcher;
import net.datatp.nlp.token.IToken;
import net.datatp.nlp.token.tag.EntityTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class VNAddressChunker extends QueryChunker {
  public VNAddressChunker() { }
  
  public void configure(NLP nlp) throws Exception {
    super.configure(nlp);
    String[] street   = {"đường", "phố", "ngõ", "nghách"} ;
    String[] quarter  = {"phường", "p.", "tổ", "thôn", "xã"} ;
    String[] district = {"quận", "q.", "huyện", "thị trấn"} ;
    String[] city     = {"thành phố", "tp", "tp.", "thị xã"} ;

    defineSynset("ck:addr:street", null, street) ;
    defineSynset("ck:addr:quarter", null, quarter) ;
    defineSynset("ck:addr:district", null, district) ;
    defineSynset("ck:addr:city", null, city) ;

    String addrNumExp =  "\\d{1,4}\\w{0,2}(/\\d{1,5})?(/\\d{1,5})?" ;
    defineMatches(
      //match by entity
      "/ regex{" + addrNumExp + "} .2. entity{type=street} .2. entity{type=quarter} .2. entity{type=district} .2. entity{type=city}",
      "/ regex{" + addrNumExp + "} .2. entity{type=street} .2. entity{type=quarter,district} .2. entity{type=city}",
      "/ regex{" + addrNumExp + "} .2. entity{type=street} .3. entity{type=quarter,district,city}",
      "/ regex{" + addrNumExp + "} .2. entity{type=street}",
      //match by synset
      "/ regex{" + addrNumExp + "} .2. synset{name=ck:addr:street} .3. synset{name=ck:addr:quarter} .3. synset{type=ck:addr:district} .3. synset{name=ck:addr:city} "
    );
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
    IToken set = createChunking(token, from, to) ;
    set.add(new EntityTag("address", set.getOriginalForm()));
    holder.add(set) ;
  }
}