package net.datatp.crawler.fetcher;

public class FetcherReport {
  private FetcherStatus      status;
  private URLFetcherReport[] urlFetcherReport;
  
  public FetcherStatus getStatus() { return status; }
  public void setStatus(FetcherStatus status) { this.status = status; }
  
  public URLFetcherReport[] getUrlFetcherReport() { return urlFetcherReport; }
  public void setUrlFetcherReport(URLFetcherReport[] urlFetcherReport) {
    this.urlFetcherReport = urlFetcherReport;
  }
}
