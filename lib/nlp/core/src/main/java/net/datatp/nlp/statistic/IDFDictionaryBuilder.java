package net.datatp.nlp.statistic;

import java.util.Iterator;

import net.datatp.util.stat.Statistic;
import net.datatp.util.stat.StatisticEntry;
import net.datatp.util.stat.Statistics;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class IDFDictionaryBuilder {
  private Statistics statistics = new Statistics() ;
  private int documentCount ;

  public void collect(Feature[] feature) {
    documentCount++ ;
    for(Feature sel : feature) {
      statistics.incr("Document", sel.getFeature(), 1) ;
      statistics.incr("Terms",    sel.getFeature(), sel.getFrequency()) ;
    }
  }

  public IDFDictionary getIDFDictionary() {
    IDFDictionary dict = new IDFDictionary() ;
    dict.setDocumentCount(documentCount) ;
    Statistic docStatistics = statistics.getStatistics("Document") ;
    Iterator<StatisticEntry> i = docStatistics.values().iterator() ;
    while(i.hasNext()) {
      StatisticEntry docStats = i.next() ;
      dict.add(docStats.getName(), (int)docStats.getFrequency()) ;
    }
    return dict ;
  }
}
