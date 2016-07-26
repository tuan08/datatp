package net.datatp.crawler.basic;

import net.datatp.crawler.scheduler.URLPostFetchScheduler;
import net.datatp.crawler.urldb.URLDatumDB;

public class InMemURLPostFetchScheduler extends URLPostFetchScheduler {
  
  public InMemURLPostFetchScheduler(URLDatumDB urlDatumDB) {
    this.urlDatumDB    = urlDatumDB;
  }
}
