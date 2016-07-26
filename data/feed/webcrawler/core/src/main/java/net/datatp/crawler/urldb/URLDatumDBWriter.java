package net.datatp.crawler.urldb;

import java.io.IOException;

public interface URLDatumDBWriter {
  
  public URLDatum createURLDatumInstance(long timestamp);
  
  public void write(URLDatum urlDatum) throws IOException ;
  
  public void optimize() throws Exception ;
  
  public void close() throws IOException ;
}