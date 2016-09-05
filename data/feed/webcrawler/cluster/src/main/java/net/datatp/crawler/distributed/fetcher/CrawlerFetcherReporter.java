package net.datatp.crawler.distributed.fetcher;

import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.datatp.crawler.distributed.registry.CrawlerRegistry;

@Component
public class CrawlerFetcherReporter implements Runnable {
  @Value("${crawler.fetcher.vm-name:unknown}")
  private String vmName;
  
  @Autowired
  private CrawlerRegistry wcRegistry;
  
  @Autowired
  private DistributedFetcher fetcher;
  
  private boolean terminate = false;
  private Thread  reportThread;
  
  @PostConstruct
  public void onInit() throws Exception {
    if("unknown".equals(vmName)) {
      vmName = InetAddress.getLocalHost().getHostName();
    }
    wcRegistry.getFetcherRegistry().initReport(vmName, fetcher.getURLFetcherMetrics());
  }
  
  @PreDestroy
  public void onDestroy() {
    terminate = true;
    reportThread.interrupt();
  }
  
  void reportHttpFetchers() throws Exception {
    wcRegistry.getFetcherRegistry().report(vmName, fetcher.getURLFetcherMetrics());
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
