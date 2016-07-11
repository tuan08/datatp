package net.datatp.webcrawler;

import org.springframework.context.ApplicationContext;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.util.io.FileUtil;
import net.datatp.webcrawler.fetcher.CrawlerFetcherApp;
import net.datatp.webcrawler.integration.DocumentConsumerLoggerApp;
import net.datatp.webcrawler.master.CrawlerMaster;
import net.datatp.webcrawler.master.CrawlerMasterApp;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.zk.tool.server.EmbededZKServer;

public class WebCrawler {
  static public String SERIALIZABLE_PACKAGES = 
    "net.datatp.webcrawler.urldb,net.datatp.webcrawler.fetcher,java.util,net.datatp.xhtml";
    
  static {
    System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES",SERIALIZABLE_PACKAGES);
  }
    
  public static void main(String[] args) throws Exception {
    FileUtil.removeIfExist("build/activemq", false);
    FileUtil.removeIfExist("build/crawler", false);
    
    EmbededZKServer zkServer = new EmbededZKServer("build/zookeeper/data", 2182);
    zkServer.clean();
    zkServer.start();
    
    EmbeddedActiveMQServer.run(null);
   
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
    
    
    ApplicationContext xhtmlLoggerAppContext = DocumentConsumerLoggerApp.run(null);
    
    Thread.currentThread().join();
  }
}