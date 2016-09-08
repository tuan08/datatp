package net.datatp.crawler.fetcher.metric;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.urldb.URLDatum;

public class URLFetcherMetric implements Serializable {
  private static final long     serialVersionUID = 1L;
  
  static int                    MAX_RECENT_URL   = 50;
  
  private String                name;
  private long                  fetchCount       = 0L;
  private LinkedList<URLDatum>  recentFetchUrls  = new LinkedList<URLDatum>();
  private Map<Integer, Integer> rcs              = new HashMap<Integer, Integer>();
  private Map<Integer, Integer> errors           = new HashMap<Integer, Integer>();
  private Map<Integer, Integer> pageTypes        = new HashMap<Integer, Integer>();
  
  public URLFetcherMetric() {}
  
  public URLFetcherMetric(String name) {
    this.name = name;
  }
  
  public String getName() { return name; }

  public long getFetchCount() { return fetchCount; }

  public URLDatum[] getRecentFetchUrls() { 
    synchronized(recentFetchUrls) {
      URLDatum[] array = new URLDatum[recentFetchUrls.size()];
      return recentFetchUrls.toArray(array);
    }
  }
  
  public Counter[] getResponseCodeGroups() {
    Counter[] counters = {
      countResponseCodeGroup("Response Code 100", 100, 200),
      countResponseCodeGroup("Response Code 200", 200, 300),
      countResponseCodeGroup("Response Code 300", 300, 400),
      countResponseCodeGroup("Response Code 400", 400, 500),
      countResponseCodeGroup("Response Code 500", 500, 600),
      countResponseCodeGroup("Response Code Other", 600, 1000000),
    };
    return counters;
  }
  
  public Counter[] getErrorCodeGroups() {
    Counter[] counters = {
        countErrorCodeGroup("Error Code 1 - 128", 1, 128),
    };
    return counters;
  }
  
  public Counter[] getPageTypes() {
    long list = 0, detail = 0, ignore = 0, uncategorized = 0;
    for(Map.Entry<Integer, Integer> entry : pageTypes.entrySet()) {
      int key   = entry.getKey();
      byte type = (byte) key;
      if(type == URLDatum.PAGE_TYPE_IGNORE) ignore++;
      else if(type == URLDatum.PAGE_TYPE_LIST) list++;
      else if(type == URLDatum.PAGE_TYPE_DETAIL) detail++;
      else uncategorized++;
    }
    Counter[] counters = {
      new Counter("ignore", ignore), new Counter("list", list), 
      new Counter("detail", detail), new Counter("uncategorized", uncategorized)
    };
    return counters;
  }

  public void log(URLDatum datum) {
    this.fetchCount++ ;
    synchronized(recentFetchUrls) {
      recentFetchUrls.addFirst(datum) ;
      if(recentFetchUrls.size() > MAX_RECENT_URL) recentFetchUrls.removeLast() ;
    }
    incrCount(rcs, datum.getLastResponseCode()) ;
    if(datum.getLastErrorCode() != ErrorCode.ERROR_TYPE_NONE) {
      incrCount(errors, datum.getLastErrorCode()) ;
    }
    byte type = (byte) datum.getPageType();
    incrCount(pageTypes, type) ;
  }
  
 
  private void incrCount(Map<Integer, Integer> map, int key) {
    Integer count =  map.get(key) ;
    if(count == null) {
      map.put(key, 1) ;
    } else {
      map.put(key, count.intValue() + 1) ;
    }
  }
  
  private Counter countResponseCodeGroup(String name, int from, int to) {
    long total = 0 ;
    Iterator<Map.Entry<Integer, Integer>> i = rcs.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<Integer, Integer> entry = i.next() ;
      int code = ((Integer) entry.getKey()).intValue() ;
      if(code >= from && code < to) {
        total += ((Integer)entry.getValue()).intValue() ;
      }
    }
    return new Counter(name, total) ;
  }
  
  private Counter countErrorCodeGroup(String name, int from, int to) {
    long total = 0 ;
    Iterator<Map.Entry<Integer, Integer>> i = errors.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<Integer, Integer> entry = i.next() ;
      int code = ((Integer) entry.getKey()).intValue() ;
      if(code >= from && code < to) {
        total += ((Integer)entry.getValue()).intValue() ;
      }
    }
    return new Counter(name, total) ;
  }
  
  public void merge(URLFetcherMetric other) {
    fetchCount += other.fetchCount;
    for(URLDatum sel : other.getRecentFetchUrls()) {
      recentFetchUrls.add(sel);
    }
    merge(rcs,       other.rcs);
    merge(errors,    other.errors);
    merge(pageTypes, other.pageTypes);
  }
  
  void merge(Map<Integer, Integer> map, Map<Integer, Integer> other) {
    for(Map.Entry<Integer, Integer> entry : other.entrySet()) {
      Integer value = map.get(entry.getKey());
      if(value != null) value = value + entry.getValue();
      else              value = entry.getValue();
      map.put(entry.getKey(), value);
    }
  }
  
  static class Counter {
    private String name ;
    private long   count;
    
    public Counter() {}
    
    public Counter(String name, long count) {
      this.name  = name;
      this.count = count;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
    
  }
}