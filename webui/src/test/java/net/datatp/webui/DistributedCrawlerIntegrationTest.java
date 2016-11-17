package net.datatp.webui;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.crawler.distributed.CrawlerApp;
import net.datatp.crawler.distributed.DistributedCrawlerApi;
import net.datatp.crawler.distributed.fetcher.CrawlerFetcherApp;
import net.datatp.crawler.distributed.integration.DocumentConsumerLoggerApp;
import net.datatp.crawler.distributed.master.CrawlerMasterApp;
import net.datatp.crawler.site.ExtractConfig;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;
import net.datatp.zk.tool.server.EmbededZKServer;

@Ignore
public class DistributedCrawlerIntegrationTest {
  
  @Test
  public void run() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
    
    FileUtil.removeIfExist("build/working", false);
    
    EmbeddedActiveMQServer.setSerializablePackages(CrawlerApp.SERIALIZABLE_PACKAGES);
    EmbededZKServer zkServer = new EmbededZKServer("build/working/zookeeper/data", 2181);
    zkServer.clean();
    zkServer.start();
    
    EmbeddedActiveMQServer.run(null);

    CrawlerMasterApp.run(null);
    
    CrawlerFetcherApp.run(null);
    
    DistributedCrawlerApi api = new  DistributedCrawlerApi(zkServer.getConnectString());
    
    SiteConfig[] configs = {
      new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 2).setExtractConfig(ExtractConfig.article()),
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 2).setExtractConfig(ExtractConfig.article()),
      new SiteConfig("vietnam", "otofun.net",    "https://www.otofun.net/forums/", 2).setExtractConfig(ExtractConfig.forum())
    };

    
    api.siteCreateGroup("vietnam");
    api.siteAdd(configs);
    
    api.crawlerStart();
    
    ApplicationContext xhtmlLoggerAppContext = DocumentConsumerLoggerApp.run(null);
    
    Thread.sleep(60000);
    Thread.currentThread().join();
  }
}