package net.datatp.crawler.site.analysis;

import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteCrawler;
import net.datatp.xhtml.extract.WDataContext;

public class SiteStructureAnalyzer {
  private SiteCrawler   siteCrawler;
  private Thread        analyseThread;
  private SiteStructure siteStructure;
  
  private long          lastAccessTime;
  
  public SiteStructureAnalyzer(SiteConfig sConfig, int maxDownload) {
    siteCrawler = new SiteCrawler(sConfig, maxDownload) {
      @Override
      public void onWData(WDataContext ctx) { 
        siteStructure.analyse(ctx); 
      }
    };
    siteStructure       = new SiteStructure(siteCrawler.getSiteContext());
    this.lastAccessTime = System.currentTimeMillis();
  }

  public SiteStructure getSiteStructure() { return siteStructure; }


  public void update(SiteConfig config) {
    siteCrawler.update(config);
    siteStructure.update(siteCrawler.getSiteContext());
  }
  
  public void run() throws Exception {
    if(analyseThread == null || !analyseThread.isAlive()) {
      analyseThread = new Thread() {
        public void run() {
          siteCrawler.crawl();
          notifyAnalyseTermination();
        };
      };
      analyseThread.start();
    }
  }
  
  public void stop() {
    if(analyseThread != null && analyseThread.isAlive()) {
      siteCrawler.stopCrawl();
      analyseThread.interrupt();
    }
  }
  
  public synchronized void notifyAnalyseTermination() {
    notifyAll();
  }
  
  public synchronized void waitForAnalyseTermination(long timeout) throws InterruptedException {
    wait(timeout);
  }
  
  public boolean isRunning() {
    return analyseThread != null && analyseThread.isAlive();
  }
  
  public long getLastAccessTime() { return lastAccessTime; }
  public void setLastAccessTime(long lastAccessTime) { this.lastAccessTime = lastAccessTime; }
}
