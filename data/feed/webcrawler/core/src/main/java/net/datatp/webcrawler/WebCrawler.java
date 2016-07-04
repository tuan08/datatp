package net.datatp.webcrawler;

import net.datatp.activemq.ActiveMQEmbeddedServer;
import net.datatp.util.io.FileUtil;
import net.datatp.webcrawler.fetcher.CrawlerFetcherApp;
import net.datatp.webcrawler.master.CrawlerMasterApp;

public class WebCrawler {
  public static void main(String[] args) throws Exception {
    FileUtil.removeIfExist("build/activemq", false);
    FileUtil.removeIfExist("build/crawler", false);
    
    ActiveMQEmbeddedServer.run(null);
    CrawlerMasterApp.run(null);
    CrawlerFetcherApp.run(null);
    Thread.currentThread().join();
  }
}