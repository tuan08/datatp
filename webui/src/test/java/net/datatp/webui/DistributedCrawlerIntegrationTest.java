package net.datatp.webui;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.crawler.distributed.CrawlerApp;
import net.datatp.crawler.distributed.DistributedCrawlerApi;
import net.datatp.crawler.distributed.DistributedCrawlerApiApp;
import net.datatp.crawler.distributed.fetcher.CrawlerFetcherApp;
import net.datatp.crawler.distributed.integration.DocumentConsumerLoggerApp;
import net.datatp.crawler.distributed.master.CrawlerMasterApp;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;
import net.datatp.zk.tool.server.EmbededZKServer;

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
    
    DistributedCrawlerApi api = DistributedCrawlerApiApp.run(null).getBean(DistributedCrawlerApi.class);
    api.siteCreateGroup("vietnam");
    api.siteAdd(new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3));
    api.siteAdd(new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 3)); 
    
    api.siteReload();
    Thread.sleep(1000);
    api.schedulerStart();
    api.fetcherStart();
    
    ApplicationContext xhtmlLoggerAppContext = DocumentConsumerLoggerApp.run(null);
    
    Thread.sleep(60000);
    Thread.currentThread().join();
  }
}