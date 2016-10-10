package net.datatp.crawler.fetcher.metric;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeMap;

import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.urldb.URLDatum;

public class URLFetcherMetric implements Serializable {
  private static final long serialVersionUID = 1L;

  static int                      MAX_RECENT_URL  = 25;

  private String                  name;
  private long                    fetchCount      = 0L;
  private long                    errorCount      = 0L;
  private long                    rc200Count      = 0L;
  private LinkedList<URLDatum>    recentFetchUrls = new LinkedList<URLDatum>();
  private TreeMap<String, Metric> metrics         = new TreeMap<>();

  public URLFetcherMetric(String name) { this.name = name; }
  
  public String getName() { return name; }

  public long getFetchCount() { return fetchCount; }

  public long getErrorCount() { return errorCount; }

  public long getRc200Count() { return rc200Count; }

  public URLDatum[] getRecentFetchUrls() { 
    synchronized(recentFetchUrls) {
      URLDatum[] array = new URLDatum[recentFetchUrls.size()];
      return recentFetchUrls.toArray(array);
    }
  }
  
  public Metric[] getMetrics() {
    synchronized(metrics) {
      Metric[] array = new Metric[metrics.size()];
      return metrics.values().toArray(array);
    }
  }
 
  void incrMetric(String category, int code, int count) {
    incrMetric(category, Integer.toString(code), count);
  }
  
  void incrMetric(String category, String name, int count) {
    String key = category + "/" + name;
    Metric metric = metrics.get(key);
    if(metric == null) {
      synchronized(metrics) {
        metric = new Metric(category, name);
        metrics.put(key, metric);
      }
    }
    metric.incr(count);
  }
  
  public void log(URLDatum datum) {
    this.fetchCount++ ;
    synchronized(recentFetchUrls) {
      recentFetchUrls.addFirst(datum) ;
      if(recentFetchUrls.size() > MAX_RECENT_URL) recentFetchUrls.removeLast() ;
    }
    
    int rcCode = datum.getLastResponseCode();
    incrMetric("RC", rcCode, 1);
    if(rcCode == 200) rc200Count++;
    
    if(datum.getLastErrorCode() != ErrorCode.ERROR_TYPE_NONE) {
      incrMetric("Error Code", datum.getLastErrorCode(), 1) ;
      errorCount++;
    }
    incrMetric("Page Type", datum.pageType(), 1) ;
  }
  
  public void merge(URLFetcherMetric other) {
    fetchCount += other.fetchCount;
    for(URLDatum sel : other.getRecentFetchUrls()) {
      recentFetchUrls.add(sel);
    }
    for(Metric sel : other.getMetrics()) {
      incrMetric(sel.getCategory(), sel.getName(), sel.getCount());
    }
  }
  
  static public class Metric {
    private String category;
    private String name;
    private int    count;
    
    public Metric() {}
    
    public Metric(String category, String name) {
      this.category = category;
      this.name     = name;
    }
    
    public String getCategory() { return category; }
    
    public String getName() { return name; }
    public int getCount() { return count; }
    
    public void incr(int count) { this.count += count ; }
  }
}