package net.datatp.crawler.site.analysis;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.datatp.crawler.site.SiteConfig;

public class SiteStructureAnalyzerService {
  private Map<String, SiteStructureAnalyzer> analyzers;
  private long                               maxLiveTime;
  private Thread                             cleanThread;

  public SiteStructureAnalyzerService(long maxLiveTime) {
    this.maxLiveTime = maxLiveTime;
    analyzers = new ConcurrentHashMap<>() ;
    cleanThread = new Thread() {
      public void run() {
        autoclean();
      }
    };
    cleanThread.start();
  }
  
  synchronized public SiteStructureAnalyzer getSiteStructureAnalyzer(String site) {
    SiteStructureAnalyzer analyzer = analyzers.get(site);
    analyzer.setLastAccessTime(System.currentTimeMillis());
    return analyzer;
  }

  synchronized public SiteStructureAnalyzer getSiteStructureAnalyzer(SiteStructureAnalyzerConfig config) throws Exception {
    SiteStructureAnalyzer analyzer = analyzers.get(config.getSiteConfig().getHostname());
    if(analyzer != null && !config.isForceNew()) return analyzer;

    if(analyzer != null) analyzer.stop();
    analyzer = new SiteStructureAnalyzer(config.getSiteConfig(), config.getMaxDownload());
    analyzers.put(config.getSiteConfig().getHostname(), analyzer);
    analyzer.run();
    return analyzer;
  }
  
  public SiteStructureAnalyzer newSiteStructureAnalyzer(SiteConfig siteConfig, int maxDownload) throws Exception {
    SiteStructureAnalyzerConfig config = new SiteStructureAnalyzerConfig(siteConfig, maxDownload);
    config.setForceNew(true);
    return getSiteStructureAnalyzer(config);
  }
  
  public void onDestroy() {
    cleanThread.interrupt();
  }
  
  private void autoclean() {
    while(true) {
      try {
        Iterator<SiteStructureAnalyzer> i = analyzers.values().iterator();
        while(i.hasNext()) {
          SiteStructureAnalyzer analyzer = i.next();
          if(!analyzer.isRunning()) {
            long liveTime = System.currentTimeMillis() - analyzer.getLastAccessTime();
            if(liveTime > maxLiveTime) {
              i.remove();
            }
          }
        }
        Thread.sleep(60 * 1000);
      } catch(InterruptedException ex) {
        return;
      }
    }
  }
}
