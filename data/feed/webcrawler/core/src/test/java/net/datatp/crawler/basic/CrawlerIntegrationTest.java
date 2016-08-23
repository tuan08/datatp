package net.datatp.crawler.basic;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.crawler.processor.ESXDocProcessor;
import net.datatp.crawler.site.ExtractConfig;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.es.ESQueryExecutor;
import net.datatp.search.ESXDocSearcher;
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
    Crawler crawler = new Crawler();
    crawler.configure(new CrawlerConfig());

    crawler.siteCreateGroup("vietnam");
    SiteConfig[] configs = {
      new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 2).setExtractConfig(ExtractConfig.article()),
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 2).setExtractConfig(ExtractConfig.article()),
      new SiteConfig("vietnam", "otofun.net",    "https://www.otofun.net/forums/", 2).setExtractConfig(ExtractConfig.forum())
    };
    crawler.siteAdd(configs);
    
    ESXDocProcessor xdocProcessor = new ESXDocProcessor("xdoc", new String[] { "127.0.0.1:9300" });
    crawler.setXDocProcessor(xdocProcessor);
    crawler.start();
    
    Thread.sleep(10000);
    ESXDocSearcher searcher = new ESXDocSearcher("xdoc", new String[] {"127.0.0.1:9300"});
    for(int i = 0; i < 5; i++) {
      ESQueryExecutor executor = searcher.getQueryExecutor();
      System.out.println("page list   = " + executor.matchTerm("attr.pageType", "list").execute().getHits().totalHits());
      System.out.println("page detail = " + executor.matchTerm("attr.pageType", "detail").execute().getHits().totalHits());
      Thread.sleep(5000);
    }
    Thread.currentThread().join();
  }
}
