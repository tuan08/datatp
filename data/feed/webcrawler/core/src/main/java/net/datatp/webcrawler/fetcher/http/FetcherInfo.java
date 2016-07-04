package net.datatp.webcrawler.fetcher.http;

import java.util.LinkedList;

import net.datatp.webcrawler.urldb.URLDatum;
import net.datatp.webcrawler.urldb.URLDatumFetchStatistic;

public class FetcherInfo extends URLDatumFetchStatistic {
  static int     MAX_RECENT_URL = 50 ;
  
  private int    fetchCount ;
  private LinkedList<URLDatum> recentFetchUrls = new LinkedList<URLDatum>() ;

  public int  getFetchCount() { return fetchCount ; }
  public void setFetchCount(int fetchCount) { this.fetchCount = fetchCount ; }

  public LinkedList<URLDatum> getRecentFetchUrl() { return this.recentFetchUrls ; }
  public void setRecentFetchUrl(LinkedList<URLDatum> list) { this.recentFetchUrls = list ; }

  public String[] recentFetchUrl() {
    String[] array = new String[recentFetchUrls.size()] ;
    for(int i = 0; i < recentFetchUrls.size(); i++) {
      array[i] = recentFetchUrls.get(i).getFetchUrl() ;
    }
    return array ; 
  }

  public void log(URLDatum datum) {
    super.log(datum) ;
    fetchCount++ ;
    recentFetchUrls.addFirst(datum) ;
    if(recentFetchUrls.size() > MAX_RECENT_URL) recentFetchUrls.removeLast() ;
  }
}