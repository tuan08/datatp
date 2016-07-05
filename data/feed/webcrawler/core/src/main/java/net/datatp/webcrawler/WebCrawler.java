package net.datatp.webcrawler;

import org.springframework.context.ApplicationContext;

import net.datatp.activemq.ActiveMQEmbeddedServer;
import net.datatp.util.io.FileUtil;
import net.datatp.webcrawler.fetcher.CrawlerFetcherApp;
import net.datatp.webcrawler.master.CrawlerMaster;
import net.datatp.webcrawler.master.CrawlerMasterApp;
import net.datatp.webcrawler.site.SiteContextManager;

public class WebCrawler {
  public static void main(String[] args) throws Exception {
    FileUtil.removeIfExist("build/activemq", false);
    FileUtil.removeIfExist("build/crawler", false);
    
    ActiveMQEmbeddedServer.run(null);
    ApplicationContext masterAppContext = CrawlerMasterApp.run(null);
    SiteContextManager siteContextManager = masterAppContext.getBean(SiteContextManager.class);
    siteContextManager.addCongfig("vnexpress.net", "http://vnexpress.net", 2, "ok");
    siteContextManager.addCongfig("dantri.com.vn", "http://dantri.com.vn", 2, "ok");
    CrawlerMaster master = masterAppContext.getBean(CrawlerMaster.class) ;
    master.getURLFetchScheduler().injectURL() ;
    master.start();
    
    ApplicationContext fetcherAppContext = CrawlerFetcherApp.run(null);
    siteContextManager = fetcherAppContext.getBean(SiteContextManager.class);
    siteContextManager.addCongfig("vnexpress.net", "http://vnexpress.net", 2, "ok");
    siteContextManager.addCongfig("dantri.com.vn", "http://dantri.com.vn", 2, "ok");
    Thread.currentThread().join();
  }
}