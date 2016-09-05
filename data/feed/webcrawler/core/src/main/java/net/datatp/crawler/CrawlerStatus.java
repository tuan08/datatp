package net.datatp.crawler;

import net.datatp.crawler.fetcher.FetcherStatus;
import net.datatp.crawler.scheduler.URLSchedulerStatus;

public class CrawlerStatus {
  private URLSchedulerStatus urlSchedulerStatus;
  private FetcherStatus[]    fetcherStatus;
  
  public URLSchedulerStatus getUrlSchedulerStatus() { return urlSchedulerStatus; }
  public void setUrlSchedulerStatus(URLSchedulerStatus urlSchedulerStatus) { 
    this.urlSchedulerStatus = urlSchedulerStatus; 
  }
  
  public FetcherStatus[] getFetcherStatus() { return fetcherStatus; }
  public void setFetcherStatus(FetcherStatus[] fetcherStatus) {
    this.fetcherStatus = fetcherStatus;
  }
}