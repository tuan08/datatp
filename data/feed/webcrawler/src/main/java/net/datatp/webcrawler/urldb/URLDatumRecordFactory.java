package net.datatp.webcrawler.urldb;

import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumFactory;

public class URLDatumRecordFactory implements URLDatumFactory {
  @Override
  public URLDatum createInstance() { return new URLDatumRecord(); }

  @Override
  public URLDatum createInstance(long timestamp) { return new URLDatumRecord(timestamp); }
}
