package net.datatp.webui;

import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.randomizedtesting.RandomizedRunner;

import net.datatp.crawler.basic.Crawler;
import net.datatp.crawler.basic.CrawlerApp;
import net.datatp.crawler.processor.ESXDocProcessor;
import net.datatp.crawler.site.ExtractConfig;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.es.NodeBuilder;

//@Ignore
@RunWith(RandomizedRunner.class)
public class CrawlerIntegrationTest {
  static {
    System.setProperty("log4j.configurationFile", "src/test/resources/log4j2.yml");
  }
  
  final Logger logger = LoggerFactory.getLogger(getClass());
  private Node node;
  
  @Before
  public void setup() throws Exception {
    logger.info("setup(): ");
    node = new NodeBuilder().newNode();
    logger.info("Node Name: " + node.settings().get("node.name"));
    logger.info("Port     : " + node.settings().get("transport.tcp.port"));
  }

  @After
  public void teardown() throws Exception {
    node.close();
  }
  
  @Test
  public void test() throws Exception {
    logger.info("test(): ");
    
    String[] args = {
      "--spring.http.multipart.location=build/upload" //# Intermediate location of uploaded files.
     // "--spring.http.multipart.multipart.file-size-threshold=0", // # Threshold after which files will be written to disk.
     // "--spring.http.multipart.multipart.max-file-size=1Mb",     //# Max file size.
     // "--spring.http.multipart.multipart.max-request-size=10Mb"  // # Max request size.  
    };
    int REFRESH_PERIOD_IN_SEC = 60 * 60;
    Crawler crawler = CrawlerApp.run(args).getBean(Crawler.class);
    
    crawler.siteCreateGroup("vietnam");
    crawler.siteAdd(
      new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    );
    
    crawler.siteAdd(
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    ); 
    
    crawler.siteAdd(
      new SiteConfig("vietnam", "vietnamnet.vn", "http://vietnamnet.vn", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    );
    
    crawler.siteAdd(
      new SiteConfig("vietnam", "cafef.vn", "http://cafef.vn", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    );
    
    crawler.siteAdd(
      new SiteConfig("US", "edition.cnn.com", "http://edition.cnn.com", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    ); 
    
    
    crawler.siteAdd(
      new SiteConfig("US", "nytimes.com", "http://www.nytimes.com", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    ); 
    
    crawler.siteAdd(
      new SiteConfig("US", "amazon.com", "https://www.amazon.com/gp/site-directory", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    ); 
    
    
    crawler.siteAdd(
      new SiteConfig("US", "ebay.com", "http://www.ebay.com/rpp/electronics-en", 2).
      setRefreshPeriod(REFRESH_PERIOD_IN_SEC).
      setExtractConfig(ExtractConfig.article())
    ); 

//    crawler.siteAdd(
//      new SiteConfig("otofun", "otofun.net", "https://www.otofun.net/forums/", 2).
//      setExtractConfig(ExtractConfig.forum())
//    );
    
    ESXDocProcessor xdocProcessor = new ESXDocProcessor("xdoc", new String[] { "127.0.0.1:9300" });
    crawler.setXDocProcessor(xdocProcessor);
    //crawler.crawlerStart();
    
    Thread.currentThread().join();
  }
}