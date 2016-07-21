package net.datatp.webcrawler;

import org.springframework.context.ApplicationContext;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.http.crawler.site.SiteConfig;
import net.datatp.util.io.FileUtil;
import net.datatp.webcrawler.fetcher.CrawlerFetcherApp;
import net.datatp.webcrawler.integration.DocumentConsumerLoggerApp;
import net.datatp.webcrawler.master.CrawlerMasterApp;
import net.datatp.webcrawler.registry.WebCrawlerRegistry;
import net.datatp.webcrawler.registry.event.FetcherEvent;
import net.datatp.webcrawler.registry.event.MasterEvent;
import net.datatp.webcrawler.registry.event.MasterEvent.Start.Option;
import net.datatp.webcrawler.registry.event.SiteConfigEvent;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.tool.server.EmbededZKServer;

public class WebCrawler {
  public static void main(String[] args) throws Exception {
    FileUtil.removeIfExist("build/activemq", false);
    FileUtil.removeIfExist("build/crawler", false);
    FileUtil.removeIfExist("build/zookeeper", false);
    
    EmbededZKServer zkServer = new EmbededZKServer("build/zookeeper/data", 2181);
    zkServer.clean();
    zkServer.start();
    
    EmbeddedActiveMQServer.run(null);

    CrawlerMasterApp.run(null);
    
    CrawlerFetcherApp.run(null);
    
    ApplicationContext xhtmlLoggerAppContext = DocumentConsumerLoggerApp.run(null);
    
    RegistryClient registryClient = new RegistryClient(zkServer.getConnectString());
    WebCrawlerRegistry wcReg = new WebCrawlerRegistry("web-crawler", registryClient);
    
    wcReg.getSiteConfigRegistry().createGroup("vietnam");
    wcReg.getSiteConfigRegistry().add(new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3));
    wcReg.getSiteConfigRegistry().add(new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 3));
    
    wcReg.getSiteConfigRegistry().getEventBroadcaster().broadcast(new SiteConfigEvent.Reload());
    wcReg.getMasterRegistry().getEventBroadcaster().broadcast(new MasterEvent.Start(Option.InjectURL));
    wcReg.getFetcherRegistry().getEventBroadcaster().broadcast(new FetcherEvent.Start());
    
    Thread.sleep(60000);
    System.out.println(registryClient.formatRegistryAsText());
    Thread.currentThread().join();
  }
}