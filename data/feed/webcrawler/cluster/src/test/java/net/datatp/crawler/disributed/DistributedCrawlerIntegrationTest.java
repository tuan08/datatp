package net.datatp.crawler.disributed;

import org.junit.Test;
import org.springframework.context.ApplicationContext;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.crawler.distributed.CrawlerApp;
import net.datatp.crawler.distributed.fetcher.CrawlerFetcherApp;
import net.datatp.crawler.distributed.integration.DocumentConsumerLoggerApp;
import net.datatp.crawler.distributed.master.CrawlerMasterApp;
import net.datatp.crawler.distributed.registry.CrawlerRegistry;
import net.datatp.crawler.distributed.registry.event.FetcherEvent;
import net.datatp.crawler.distributed.registry.event.SchedulerEvent;
import net.datatp.crawler.distributed.registry.event.SiteConfigEvent;
import net.datatp.crawler.distributed.registry.event.SchedulerEvent.Start.Option;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;
import net.datatp.zk.registry.RegistryClient;
import net.datatp.zk.tool.server.EmbededZKServer;

public class DistributedCrawlerIntegrationTest {
  
  @Test
  public void run() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
    FileUtil.removeIfExist("build/activemq", false);
    FileUtil.removeIfExist("build/crawler", false);
    FileUtil.removeIfExist("build/zookeeper", false);
    
    EmbededZKServer zkServer = new EmbededZKServer("build/zookeeper/data", 2181);
    zkServer.clean();
    zkServer.start();
    
    EmbeddedActiveMQServer.setSerializablePackages(CrawlerApp.SERIALIZABLE_PACKAGES);
    EmbeddedActiveMQServer.run(null);

    CrawlerMasterApp.run(null);
    
    CrawlerFetcherApp.run(null);
    
    RegistryClient registryClient = new RegistryClient(zkServer.getConnectString());
    
    CrawlerRegistry wcReg = new CrawlerRegistry(registryClient);
    
    wcReg.getSiteConfigRegistry().createGroup("vietnam");
    wcReg.getSiteConfigRegistry().add(new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 2));
    wcReg.getSiteConfigRegistry().add(new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 2));
    
    wcReg.getSiteConfigRegistry().getEventBroadcaster().broadcast(new SiteConfigEvent.Reload());
    Thread.sleep(1000);
    wcReg.getSchedulerRegistry().getEventBroadcaster().broadcast(new SchedulerEvent.Start(Option.InjectURL));
    wcReg.getFetcherRegistry().getEventBroadcaster().broadcast(new FetcherEvent.Start());
    
    ApplicationContext xhtmlLoggerAppContext = DocumentConsumerLoggerApp.run(null);
    
    System.out.println(registryClient.formatRegistryAsText());
    Thread.currentThread().join();
  }
}
