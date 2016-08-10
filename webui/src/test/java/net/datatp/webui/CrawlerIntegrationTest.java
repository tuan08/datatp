package net.datatp.webui;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.crawler.basic.Crawler;
import net.datatp.crawler.basic.CrawlerApp;
import net.datatp.crawler.processor.ESXDocProcessor;
import net.datatp.crawler.site.ExtractConfig;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.util.io.FileUtil;
import net.datatp.util.log.LoggerFactory;

public class CrawlerIntegrationTest {
  private Node node;
  
  @Before
  public void setup() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
    FileUtil.removeIfExist("build/working", false);
    
    NodeBuilder nb = nodeBuilder();
    nb.getSettings().put("cluster.name",       "elasticsearch");
    nb.getSettings().put("path.home",          "build/working/elasticsearch/data");
    nb.getSettings().put("node.name",          "localhost");
    nb.getSettings().put("transport.tcp.port", "9300");
    node = nb.node();
  }

  @After
  public void teardown() throws Exception {
    node.close();
  }
  
  @Test
  public void test() throws Exception {
    Crawler crawler = CrawlerApp.run(new String[] {}).getBean(Crawler.class);
    
    crawler.siteCreateGroup("vietnam");
    crawler.siteAdd(
      new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 2).
      setExtractConfig(ExtractConfig.article())
    );
    
    crawler.siteAdd(
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 2).
      setExtractConfig(ExtractConfig.article())
    ); 

    crawler.siteAdd(
      new SiteConfig("otofun", "otofun.net", "https://www.otofun.net/forums/", 2).
      setExtractConfig(ExtractConfig.forum())
    );
    
    ESXDocProcessor xdocProcessor = new ESXDocProcessor("xdoc", new String[] { "127.0.0.1:9300" });
    crawler.setXDocProcessor(xdocProcessor);
    crawler.start();
    
    Thread.currentThread().join();
  }
}