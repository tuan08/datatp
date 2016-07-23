package net.datatp.http.crawler.urldb;

public interface URLDatumDB {
  public URLDatumDBIterator createURLDatumDBIterator() throws Exception ;
  public URLDatumDBWriter createURLDatumDBWriter() throws Exception ;
}
