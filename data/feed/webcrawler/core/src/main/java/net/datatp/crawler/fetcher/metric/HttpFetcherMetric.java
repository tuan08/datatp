package net.datatp.crawler.fetcher.metric;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.urldb.URLDatum;

public class HttpFetcherMetric implements Serializable {
  private static final long serialVersionUID = 1L;

  static int     MAX_RECENT_URL = 50 ;
  
  private String                name;
  private int                   fetchCount;
  private LinkedList<URLDatum>  recentFetchUrls  = new LinkedList<URLDatum>();
  private Map<Integer, Integer> rcs              = new HashMap<Integer, Integer>();
  private Map<Integer, Integer> errors           = new HashMap<Integer, Integer>();
  private Map<Integer, Integer> pageTypes        = new HashMap<Integer, Integer>();
  
  public HttpFetcherMetric() {}
  
  public HttpFetcherMetric(String name) {
    this.name = name;
  }
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public int getFetchCount() { return fetchCount; }
  public void setFetchCount(int fetchCount) { this.fetchCount = fetchCount; }

  public LinkedList<URLDatum> getRecentFetchUrls() { return recentFetchUrls; }

  public void setRecentFetchUrls(LinkedList<URLDatum> list) { recentFetchUrls = list; }

  public Map<Integer, Integer> getRcs() { return rcs; }
  public void setRcs(Map<Integer, Integer> rcs) { this.rcs = rcs; }

  public Map<Integer, Integer> getErrors() { return errors; }
  public void setErrors(Map<Integer, Integer> errors) { this.errors = errors; }

  public Map<Integer, Integer> getPageTypes() { return pageTypes; }
  public void setPageTypes(Map<Integer, Integer> pageTypes) { this.pageTypes = pageTypes; }

  public String[] recentFetchUrl() {
    String[] array = new String[recentFetchUrls.size()] ;
    for(int i = 0; i < recentFetchUrls.size(); i++) {
      array[i] = recentFetchUrls.get(i).getOriginalUrl();
    }
    return array ; 
  }
  
  public int countResponseCodeGroup100() { return countResponseCodeGroup(100, 200) ; }
  
  public int countResponseCodeGroup200() { return countResponseCodeGroup(200, 300) ; }
  
  public int countResponseCodeGroup300() { return countResponseCodeGroup(300, 400) ; }
  
  public int countResponseCodeGroup400() { return countResponseCodeGroup(400, 500) ; }
  
  public int countResponseCodeGroup500() { return countResponseCodeGroup(500, 600) ; }
  
  public int countResponseCodeGroup10000() { return countResponseCodeGroup(10000, 20000) ; }
  
  public int countErrors() { return countErrorCodeGroup(1, 128) ; }

  public int countPageListType() {
    int total = 0 ;
    Integer count = ((Integer)pageTypes.get(URLDatum.PAGE_TYPE_LIST)) ;
    if(count != null) total = count.intValue() ;
    return total ;
  }
  
  public int countPageDetailType() {
    int total = 0 ;
    Integer count = ((Integer)pageTypes.get(URLDatum.PAGE_TYPE_DETAIL)) ;
    if(count != null) total = count.intValue() ;
    return total ;
  }
  
  public void log(URLDatum datum) {
    fetchCount++ ;
    recentFetchUrls.addFirst(datum) ;
    if(recentFetchUrls.size() > MAX_RECENT_URL) recentFetchUrls.removeLast() ;
    incrCount(rcs, datum.getLastResponseCode()) ;
    if(datum.getLastErrorCode() != ErrorCode.ERROR_TYPE_NONE) {
      incrCount(errors, datum.getLastErrorCode()) ;
    }
    incrCount(pageTypes, (int)datum.getPageType()) ;
  }
  
 
  private void incrCount(Map<Integer, Integer> map, int code) {
    Integer key = new Integer(code) ;
    Integer count = (Integer) map.get(key) ;
    if(count == null) {
      map.put(key, 1) ;
    } else {
      map.put(key, count.intValue() + 1) ;
    }
  }
  
  private int countResponseCodeGroup(int from, int to) {
    int total = 0 ;
    Iterator<Map.Entry<Integer, Integer>> i = rcs.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<Integer, Integer> entry = i.next() ;
      int code = ((Integer) entry.getKey()).intValue() ;
      if(code >= from && code < to) {
        total += ((Integer)entry.getValue()).intValue() ;
      }
    }
    return total ;
  }
  
  private int countErrorCodeGroup(int from, int to) {
    int total = 0 ;
    Iterator<Map.Entry<Integer, Integer>> i = errors.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<Integer, Integer> entry = i.next() ;
      int code = ((Integer) entry.getKey()).intValue() ;
      if(code >= from && code < to) {
        total += ((Integer)entry.getValue()).intValue() ;
      }
    }
    return total ;
  }
}