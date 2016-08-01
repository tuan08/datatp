package net.datatp.crawler.basic;

import org.junit.Test;

import net.datatp.crawler.site.ExtractConfig;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.util.log.LoggerFactory;

public class CrawlerUnitTest {
  @Test
  public void test() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
    
    Crawler crawler = new Crawler();
    crawler.configure(new CrawlerConfig());
        
    
    crawler.siteCreateGroup("vietnam");
    crawler.siteAdd(
        new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3).
        setExtractConfig(ExtractConfig.article())
    );
    crawler.siteAdd(
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 3).
      setExtractConfig(ExtractConfig.article())
    ); 

    //crawler.setXhtmlDocumentProcessor(WDataProcessor.PRINT_URL);
    crawler.start();
    
    Thread.currentThread().join();
  }
}
