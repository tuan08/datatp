package net.datatp.webcrawler.fetcher;

import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.datatp.webcrawler.registry.WebCrawlerRegistry;

@Component
public class CrawlerFetcherReporter implements Runnable {
  @Value("${crawler.fetcher.vm-name:unknown}")
  private String vmName;
  
  @Autowired
  private WebCrawlerRegistry wcRegistry;
  
  @Autowired
  private HttpFetcherManager httpFetcherManager;
  
  private boolean terminate = false;
  private Thread  reportThread;
  
  @PostConstruct
  public void onInit() throws Exception {
    if("unknown".equals(vmName)) {
      vmName = InetAddress.getLocalHost().getHostName();
    }
    wcRegistry.getFetcherRegistry().initReport(vmName, httpFetcherManager.getFetcherMetrics());
  }
  
  @PreDestroy
  public void onDestroy() {
    terminate = true;
    reportThread.interrupt();
  }
  
  void reportHttpFetchers() throws Exception {
    wcRegistry.getFetcherRegistry().report(vmName, httpFetcherManager.getFetcherMetrics());
  }

  @Override
  public void run() {
    while(!terminate) {
      try {
        reportHttpFetchers();
        if(!terminate) Thread.sleep(150000);
      } catch (InterruptedException e) {
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
