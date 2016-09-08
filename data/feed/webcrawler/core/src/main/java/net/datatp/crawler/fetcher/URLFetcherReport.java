package net.datatp.crawler.fetcher;

import net.datatp.crawler.fetcher.metric.URLFetcherMetric;

public class URLFetcherReport {
  private String           name;
  private Status           status;
  private URLFetcherMetric urlFetcherMetric;

  public URLFetcherReport() {}
  
  public URLFetcherReport(String name, Status status, URLFetcherMetric metric) {
    this.name             = name;
    this.status           = status;
    this.urlFetcherMetric = metric;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }

  public URLFetcherMetric getUrlFetcherMetric() { return urlFetcherMetric; }
  public void setUrlFetcherMetric(URLFetcherMetric urlFetcherMetric) { this.urlFetcherMetric = urlFetcherMetric; }
}
