package net.datatp.crawler.distributed.fetcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

import net.datatp.channel.ChannelGateway;
import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.fetcher.metric.URLFetcherMetric;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class DistributedFetcher  {
  static int MAX_QUEUE_CAPACITY = 250000 ;
  private static final Logger logger = LoggerFactory.getLogger(DistributedFetcher.class);

  private String name = "BasicFetcher" ;

  @Autowired
  private SiteContextManager manager ;
  
  @Autowired
  @Qualifier("XDocGateway")
  private ChannelGateway xDocGateway ;

  @Autowired
  @Qualifier("URLFetchCommitGateway")
  private ChannelGateway urlFetchCommitGateway ;
  

  private URLDatumFetchQueue urldatumFetchQueue = new URLDatumFetchQueue();

  @Autowired
  private FetchDataProcessor fetchDataProcessor;
  
  @Autowired
  private SiteSessionManager siteSessionManager ;

  @Value("${crawler.fetcher.num-of-threads}")
  private int numOfThreads = 1 ;
  
  private FetcherThread[] thread ;
  

  public String getName() { return name ; }
  public void   setName(String name) { this.name = name ; }

  public List<URLFetcherMetric> getURLFetcherMetrics() {
    List<URLFetcherMetric> holder = new ArrayList<URLFetcherMetric>() ;
    if(thread != null) {
      for(int i = 0; i < thread.length; i++) {
        if(thread[i] != null) {
          holder.add(thread[i].fetcher.getURLFetcherMetric()) ;
        }
      }
    }
    return holder ;
  }

  @JmsListener(destination = "crawler.url.fetch")
  public void schedule(Serializable data) throws InterruptedException {
    if(data instanceof List) {
      List<URLDatum> holder = (List<URLDatum>) data ;
      for(int i = 0; i < holder.size(); i++) {
        urldatumFetchQueue.add(holder.get(i)) ;
      }
    } else {
      urldatumFetchQueue.add((URLDatum) data) ;
    }
  }
  
  public void setNumberOfFetcher(int value) { this.numOfThreads = value ; }

  @PostConstruct
  public void onInit() {
    thread = new FetcherThread[numOfThreads] ;
    for(int i = 0; i < thread.length; i++) {
      String fetcherName = "fetcher-" + i;
      DistributedURLFetcher fetcher =  
          new DistributedURLFetcher(fetcherName, manager, siteSessionManager, urldatumFetchQueue, urlFetchCommitGateway, xDocGateway, fetchDataProcessor) ;
      thread[i] = new FetcherThread(fetcher) ;
      thread[i].setName(fetcherName) ;
    }
  }

  public void start() {
    for(int i = 0; i < thread.length; i++) {
      if(!thread[i].isAlive()) thread[i].start() ;
    }
    logger.info("Start BasicFetcher " + name) ;
  }

  public void stop() {
    if(thread == null) return ;
    for(int i = 0; i < thread.length; i++) {
      if(thread[i].isAlive()) thread[i].fetcher.setExit(true) ;
    }
  }

  static public class FetcherThread extends Thread {
    DistributedURLFetcher fetcher ;

    public FetcherThread(DistributedURLFetcher fetcher) {
      super(fetcher) ;
      this.fetcher = fetcher ;
    }
  }
}