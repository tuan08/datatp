package net.datatp.nlp.query.match;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class UnitMatch {
  private String word;
  private int    from ;
  private int    to ;
  private UnitMatcher unitMatcher ;

  public UnitMatch(String word, int from, int to) {
    this.word = word ;
    this.from = from;
    this.to   = to ;
  }

  public String getWord() { return word; }
  public void setWord(String word) { this.word = word; }

  public int getFrom() { return from; }
  public void setFrom(int from) { this.from = from; }
  public int getTo() { return to; }
  public void setTo(int to) { this.to = to; }

  public UnitMatcher getUnitMatcher() { return this.unitMatcher ; }
  public void setUnitMatcher(UnitMatcher unitMatcher) { this.unitMatcher = unitMatcher ; }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append(word).append('[').append(from).append(',').append(to).append(']') ;
    return b.toString() ;
  }
}
