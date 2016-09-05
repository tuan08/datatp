package net.datatp.crawler.fetcher;

import net.datatp.crawler.fetcher.metric.URLFetcherMetric;

public class URLFetcherReport {
  private String           name;
  private Status status;
  private URLFetcherMetric metric;

  public URLFetcherReport() {}
  
  public URLFetcherReport(String name, Status status, URLFetcherMetric metric) {
    this.name   = name;
    this.status = status;
    this.metric = metric;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Status getStatus() { return status; }

  public void setStatus(Status status) { this.status = status; }

  public URLFetcherMetric getMetric() { return metric; }

  public void setMetric(URLFetcherMetric metric) { this.metric = metric; }
}
