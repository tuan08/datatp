package net.datatp.crawler.distributed.urldb;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumFactory;

public class URLDatumRecordFactory implements URLDatumFactory {
  @Override
  public URLDatum createInstance() { return new URLDatumRecord(); }

  @Override
  public URLDatum createInstance(long timestamp) { return new URLDatumRecord(timestamp); }
}
