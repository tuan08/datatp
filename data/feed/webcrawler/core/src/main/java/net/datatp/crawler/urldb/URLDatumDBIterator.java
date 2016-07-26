package net.datatp.crawler.urldb;

import java.io.IOException;

public interface URLDatumDBIterator {

  public boolean hasNext() throws IOException ;
  public URLDatum next() throws IOException;
  public void close() throws IOException ;

}
