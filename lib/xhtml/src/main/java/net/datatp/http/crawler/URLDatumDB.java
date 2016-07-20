package net.datatp.http.crawler;

import java.util.TreeMap;

public class URLDatumDB {
  private TreeMap<String, URLDatum> holder = new TreeMap<>();

  synchronized public URLDatum getUrlDatum(String url) { return holder.get(url); }
  
  synchronized public void save(URLDatum urlDatum) {
    holder.put(urlDatum.getId(), urlDatum);
  }
  
  
}
