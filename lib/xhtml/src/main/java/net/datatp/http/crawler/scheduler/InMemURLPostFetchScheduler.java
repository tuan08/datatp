package net.datatp.http.crawler.scheduler;

import net.datatp.http.crawler.urldb.URLDatumDB;

public class InMemURLPostFetchScheduler extends URLPostFetchScheduler {
  
  public InMemURLPostFetchScheduler(URLDatumDB urlDatumDB) {
    this.urlDatumDB    = urlDatumDB;
  }
}
