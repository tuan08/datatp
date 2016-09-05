package net.datatp.crawler.fetcher;

public class FetcherStatus {
  private String id;
  private String host;
  private Status status = Status.INIT;
  
  public FetcherStatus() {}
  
  public FetcherStatus(String id, String host) {
    this.id   = id ;
    this.host = host;
  }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getHost() { return host; }
  public void setHost(String host) { this.host = host; }
  
  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
}
