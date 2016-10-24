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
    nb.getSettings().put("network.bind_host",  "0.0.0.0");
    nb.getSettings().put("transport.tcp.port", "9300");
    nb.getSettings().put("http.cors.enabled",  "true");
    nb.getSettings().put("http.cors.allow-origin", "*");
    node = nb.node();
  }

  @After
  public void teardown() throws Exception {
    node.close();
  }
  
  @Test
  public void test() throws Exception {
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