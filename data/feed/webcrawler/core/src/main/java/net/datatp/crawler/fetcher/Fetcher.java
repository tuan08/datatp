package net.datatp.crawler.fetcher;

public interface Fetcher {
  public FetcherStatus getStatus();
  public void start() ;
  public void stop() ;
}
