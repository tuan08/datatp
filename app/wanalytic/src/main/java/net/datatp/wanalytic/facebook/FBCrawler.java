package net.datatp.wanalytic.facebook;

public class FBCrawler {
  private String zkConnects ;
  private FBObjectIdQueue objectIdQueue;
  private int numOfFetcher;

  private FBObjectFetcher[] fetchers;
  
  public FBCrawler(String zkConnects, FBObjectIdQueue objectIdQueue, int numOfFetcher) {
    this.zkConnects    = zkConnects;
    this.objectIdQueue = objectIdQueue;
    this.numOfFetcher  = numOfFetcher;
  }
  
  public void start() throws Exception {
    fetchers = new FBObjectFetcher[numOfFetcher];
    for(int i = 0; i < fetchers.length; i++) {
      fetchers[i] = new FBObjectFetcher(i, zkConnects, objectIdQueue);
      fetchers[i].start();
    }
  }
  
  public void shutdown() throws Exception {
    if(fetchers == null) return ;
    for(int i = 0; i < fetchers.length; i++) {
      fetchers[i].shutdown();
    }
    fetchers = null;
  }
}
