package net.datatp.crawler.urldb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewURLDatumCache {
  private URLDatumMap    urlDatumMap ;
  private List<URLDatum> expiredURLDatums     = new ArrayList<>() ;
 
  public NewURLDatumCache(int size) {
    urlDatumMap = new URLDatumMap(size);
  }
  
  synchronized public void add(URLDatum urlDatum) {
    urlDatumMap.put(urlDatum.getId(), urlDatum);
  }
  
  synchronized public List<URLDatum> takeAllURLDatums() {
    List<URLDatum> holder = new ArrayList<>();
    holder.addAll(urlDatumMap.values());
    urlDatumMap.clear();
    return holder;
  }
  
  synchronized public List<URLDatum> takeExpiredURLDatums() {
    List<URLDatum> retList = expiredURLDatums;
    expiredURLDatums = new ArrayList<>();
    return retList;
  }
  
  public class URLDatumMap extends LinkedHashMap<String, URLDatum> {
    private static final long serialVersionUID = 1L;
    
    public URLDatumMap(int size) { super(size); }
    
    protected boolean removeEldestEntry(Map.Entry<String, URLDatum> eldest) {
      expiredURLDatums.add(eldest.getValue());
      return true;
    }
  }
}