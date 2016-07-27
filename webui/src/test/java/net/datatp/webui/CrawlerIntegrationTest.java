package net.datatp.webui;

import org.junit.Test;

import net.datatp.crawler.basic.Crawler;
import net.datatp.crawler.basic.CrawlerApp;
import net.datatp.crawler.site.SiteConfig;

public class CrawlerIntegrationTest {
  @Test
  public void test() throws Exception {
    Crawler crawler = CrawlerApp.run(new String[] {}).getBean(Crawler.class);
    
    
    crawler.siteCreateGroup("vietnam");
    crawler.siteAdd(new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3));
    crawler.siteAdd(new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 3)); 

    //crawler.setXhtmlDocumentProcessor(WPageDataProcessor.PRINT_URL);
    crawler.start();
    
    Thread.currentThread().join();
  }
}
