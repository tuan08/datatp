package net.datatp.crawler.distributed.urldb;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.storage.kvdb.Record;

public class URLDatumRecord extends URLDatum implements Record {
  public URLDatumRecord() {
  }
  
  public URLDatumRecord(long time) {
    super(time);
  }
}
