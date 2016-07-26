package net.datatp.crawler.basic;

public class CrawlerConfig {
  static String[] EXCLUDE_URL_PATTERNS = {
      ".*\\.(pdf|doc|xls|ppt)",
      ".*\\.(rss|rdf)",
      ".*\\.(img|jpg|jpeg|gif|png)",
      ".*\\.(exe)",
      ".*\\.(zip|arj|rar|lzh|z|gz|gzip|tar|bin|rar)" ,
      ".*\\.(mp3|m4a|wav|ra|ram|aac|aif|avi|mpg|mpeg|qt|plj|asf|mov|rm|mp4|wma|wmv|mpe|mpa)",
      ".*\\.(r0*|r1*|a0*|a1*|tif|tiff|msi|msu|ace|iso|ogg|7z|sea|sit|sitx|pps|bz2|xsl)"
  };
  
  private int maxUrlQueueSize           = 10000;
  private int maxXhtmlDocumentQueueSize = 1000;
  private int numOfFetcher              = 3;

  public int getMaxUrlQueueSize() { return maxUrlQueueSize; }
  public CrawlerConfig setMaxUrlQueueSize(int size) {
    this.maxUrlQueueSize = size;
    return this;
  }
  
  public int getMaxXhtmlDocumentQueueSize() { return maxXhtmlDocumentQueueSize; }
  public CrawlerConfig setMaxXhtmlDocumentQueueSize(int maxXhtmlDocumentQueueSize) {
    this.maxXhtmlDocumentQueueSize = maxXhtmlDocumentQueueSize;
    return this;
  }
  
  public int getNumOfFetcher() { return numOfFetcher; }
  public CrawlerConfig setNumOfFetcher(int num) {
    this.numOfFetcher = num;
    return this;
  }
}
