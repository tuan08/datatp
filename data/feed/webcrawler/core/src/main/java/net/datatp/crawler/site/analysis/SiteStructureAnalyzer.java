package net.datatp.crawler.site.analysis;

import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteCrawler;
import net.datatp.xhtml.extract.WDataExtractContext;

public class SiteStructureAnalyzer {
  private SiteCrawler   siteCrawler;
  private Thread        analyseThread;
  private SiteStructure siteStructure;
  
  private long          lastAccessTime;
  
  public SiteStructureAnalyzer(SiteConfig sConfig, int maxDownload) {
    siteCrawler = new SiteCrawler(sConfig, maxDownload) {
      @Override
      public void onWData(WDataExtractContext ctx) { 
        siteStructure.analyse(getSiteContext(), ctx); 
      }
    };
    siteStructure       = new SiteStructure();
    this.lastAccessTime = System.currentTimeMillis();
  }

  public SiteStructure getSiteStructure() { return siteStructure; }
  
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
