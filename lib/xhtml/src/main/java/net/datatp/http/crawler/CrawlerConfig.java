package net.datatp.http.crawler;

public class CrawlerConfig {
  private int maxUrlQueueSize       = 10000;
  private int maxDataFetchQueueSize = 1000;
  private int numOfFetcher          = 3;

  public int getMaxUrlQueueSize() { return maxUrlQueueSize; }
  public CrawlerConfig setMaxUrlQueueSize(int size) {
    this.maxUrlQueueSize = size;
    return this;
  }
  public int getMaxDataFetchQueueSize() { return maxDataFetchQueueSize; }
  public CrawlerConfig setMaxDataFetchQueueSize(int size) {
    this.maxDataFetchQueueSize = size;
    return this;
  }
  
  public int getNumOfFetcher() { return numOfFetcher; }
  public CrawlerConfig setNumOfFetcher(int num) {
    this.numOfFetcher = num;
    return this;
  }
}
