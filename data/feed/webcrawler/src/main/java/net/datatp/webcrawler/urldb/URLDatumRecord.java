package net.datatp.webcrawler.urldb;

import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.storage.kvdb.Record;

public class URLDatumRecord extends URLDatum implements Record {
  public URLDatumRecord() {
  }
  
  public URLDatumRecord(long time) {
    super(time);
  }
}
