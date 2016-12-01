package net.datatp.crawler.urldb;

import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

public class InMemURLDatumDB implements URLDatumDB {

  private TreeMap<String, URLDatum> holder = new TreeMap<>();

  synchronized public URLDatum getUrlDatum(String url) { return holder.get(url); }
  
  synchronized public void save(URLDatum urlDatum) {
    holder.put(urlDatum.getId(), urlDatum);
  }  
  
  public URLDatumDBIterator createURLDatumDBIterator() {
    return new URLDatumDBIteratorImpl();
  }
  
  public URLDatumDBWriter createURLDatumDBWriter() {
    return new URLDatumDBWriterImpl();
  }
  
  public URLDatumFactory getURLDatumFactory() { return URLDatumFactory.DEFAULT; }
  
  public class URLDatumDBWriterImpl implements URLDatumDBWriter {

    @Override
    public URLDatum createURLDatumInstance(long timestamp) {
      return new URLDatum(timestamp);
    }

    @Override
    public void write(URLDatum urlDatum) throws IOException {
      if(urlDatum.getStatus() == URLDatum.STATUS_NEW) {
        if(holder.containsKey(urlDatum.getId())) return;
      }
      holder.put(urlDatum.getId(), urlDatum);
    }

    @Override
    public void optimize() throws Exception {
    }

    @Override
    public void close() throws IOException {
    }
    
  }
  
  public class URLDatumDBIteratorImpl implements URLDatumDBIterator {
    Iterator<URLDatum> iterator = holder.values().iterator();
    
    @Override
    public boolean hasNext() throws IOException {
      return iterator.hasNext();
    }

    @Override
    public URLDatum next() throws IOException {
      return iterator.next();
    }

    @Override
    public void close() throws IOException {
    }
  }

}
