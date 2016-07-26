package net.datatp.webui;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.crawler.distributed.DistributedCrawlerApi;
import net.datatp.crawler.distributed.DistributedCrawlerApiApp;
import net.datatp.crawler.distributed.fetcher.CrawlerFetcherApp;
import net.datatp.crawler.distributed.integration.DocumentConsumerLoggerApp;
import net.datatp.crawler.distributed.master.CrawlerMasterApp;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.util.io.FileUtil;
import net.datatp.zk.tool.server.EmbededZKServer;

public class DistributedCrawlerIntegrationTest {
  
  @Test
  public void run() throws Exception {
    FileUtil.removeIfExist("build/activemq", false);
    FileUtil.removeIfExist("build/crawler", false);
    FileUtil.removeIfExist("build/zookeeper", false);
    
    EmbededZKServer zkServer = new EmbededZKServer("build/zookeeper/data", 2181);
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
    api.schedulerStart();
    api.fetcherStart();
    
    ApplicationContext xhtmlLoggerAppContext = DocumentConsumerLoggerApp.run(null);
    
    Thread.sleep(60000);
    Thread.currentThread().join();
  }
}