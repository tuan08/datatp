package net.datatp.crawler.fetcher;

import net.datatp.crawler.fetcher.metric.URLFetcherMetric;

public class FetcherReport {
  private FetcherStatus       status;
  private URLFetchQueueReport urlFetchQueueReport;
  private URLFetcherReport[]  urlFetcherReport;
  private URLFetcherMetric    aggregateUrlFetcherMetric;
  
  public FetcherStatus getStatus() { return status; }
  public void setStatus(FetcherStatus status) { this.status = status; }
  
  public URLFetchQueueReport getUrlFetchQueueReport() { return urlFetchQueueReport; }
  public void setUrlFetchQueueReport(URLFetchQueueReport urlFetchQueueReport) { this.urlFetchQueueReport = urlFetchQueueReport; }
  
  public URLFetcherReport[] getUrlFetcherReport() { return urlFetcherReport; }
  public void setUrlFetcherReport(URLFetcherReport[] urlFetcherReport) {
    this.urlFetcherReport = urlFetcherReport;
    
    aggregateUrlFetcherMetric = new URLFetcherMetric("Aggregate Url Fetcher Metric");
    for(URLFetcherReport sel : urlFetcherReport) {
      aggregateUrlFetcherMetric.merge(sel.getUrlFetcherMetric());
    }
  }
  
  public URLFetcherMetric getAggregateUrlFetcherMetric() { return aggregateUrlFetcherMetric; }  
}