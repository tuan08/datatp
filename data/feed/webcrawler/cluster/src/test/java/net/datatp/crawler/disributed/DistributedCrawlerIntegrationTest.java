package net.datatp.crawler.disributed;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

import com.carrotsearch.randomizedtesting.RandomizedRunner;

import net.datatp.activemq.EmbeddedActiveMQServer;
import net.datatp.crawler.distributed.CrawlerApp;
import net.datatp.crawler.distributed.DistributedCrawlerApi;
import net.datatp.crawler.distributed.fetcher.CrawlerFetcherApp;
import net.datatp.crawler.distributed.integration.DocumentConsumerLoggerApp;
import net.datatp.crawler.distributed.master.CrawlerMasterApp;
import net.datatp.crawler.site.ExtractConfig;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.es.NodeBuilder;
import net.datatp.util.io.FileUtil;
import net.datatp.zk.tool.server.EmbededZKServer;

@RunWith(RandomizedRunner.class)
public class DistributedCrawlerIntegrationTest {
  protected final Logger logger = Loggers.getLogger(getClass());
 
  private Node node;
  private EmbededZKServer zkServer;
  
  @Before
  public void setup() throws Exception {
    FileUtil.removeIfExist("build/working", false);
    
    zkServer = new EmbededZKServer("build/working/zookeeper/data", 2181);
    zkServer.clean();
    zkServer.start();
    
    EmbeddedActiveMQServer.setSerializablePackages(CrawlerApp.SERIALIZABLE_PACKAGES);
    EmbeddedActiveMQServer.run(null);
    
    node = new NodeBuilder().newNode();
    logger.info("Node Name: " + node.settings().get("node.name"));
    logger.info("Port     : " + node.settings().get("transport.tcp.port"));
    
  }

  @After
  public void teardown() throws Exception {
    zkServer.shutdown();
    node.close();
  }

  
  @Test
  public void run() throws Exception {
    CrawlerMasterApp.run(null);
    
    CrawlerFetcherApp.run(null);
    
    DistributedCrawlerApi api = new  DistributedCrawlerApi(zkServer.getConnectString());
    api.siteCreateGroup("vietnam");
    
    SiteConfig[] configs = {
      new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 2).setExtractConfig(ExtractConfig.article()),
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 2).setExtractConfig(ExtractConfig.article()),
      new SiteConfig("vietnam", "otofun.net", "https://www.otofun.net/forums/", 2).setExtractConfig(ExtractConfig.forum())
    };
    api.siteAdd(configs);
    Thread.sleep(1000);

    api.crawlerStart();
    
    ApplicationContext xhtmlLoggerAppContext = DocumentConsumerLoggerApp.run(null);
    
    System.out.println(api.getCrawlerRegistry().getRegistryClient().formatRegistryAsText());
    Thread.currentThread().join();
  }
}
