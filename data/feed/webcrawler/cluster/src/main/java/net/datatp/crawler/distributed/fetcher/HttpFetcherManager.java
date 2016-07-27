package net.datatp.crawler.distributed.fetcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.fetcher.metric.HttpFetcherMetric;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class HttpFetcherManager  {
  static int MAX_QUEUE_CAPACITY = 250000 ;
  private static final Logger logger = LoggerFactory.getLogger(HttpFetcherManager.class);

  private String name = "HttpFetcherManager" ;

  @Autowired
  private SiteContextManager manager ;

  private URLDatumFetchQueue urldatumFetchQueue = new URLDatumFetchQueue();

//  @Autowired
//  @Qualifier("FetchDataGateway")
//  private ChannelGateway fetchDataGateway ;

  @Autowired
  private FetchDataProcessor fetchDataProcessor;
  
  @Autowired
  private SiteSessionManager siteSessionManager ;

  @Value("${crawler.fetcher.num-of-threads}")
  private int numOfThreads = 1 ;
  
  private FetcherThread[] thread ;
  

  public String getName() { return name ; }
  public void   setName(String name) { this.name = name ; }

  public List<HttpFetcherMetric> getFetcherMetrics() {
    List<HttpFetcherMetric> holder = new ArrayList<HttpFetcherMetric>() ;
    if(thread != null) {
      for(int i = 0; i < thread.length; i++) {
        if(thread[i] != null) {
          holder.add(thread[i].fetcher.getFetcherMetric()) ;
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
      DistributedHttpFetcher fetcher =  
          new DistributedHttpFetcher(fetcherName, manager, siteSessionManager, urldatumFetchQueue, fetchDataProcessor) ;
      thread[i] = new FetcherThread(fetcher) ;
      thread[i].setName(fetcherName) ;
    }
  }

  public void start() {
    for(int i = 0; i < thread.length; i++) {
      if(!thread[i].isAlive()) thread[i].start() ;
    }
    logger.info("Start HttpFetcherManager " + name) ;
  }

  public void stop() {
    if(thread == null) return ;
    for(int i = 0; i < thread.length; i++) {
      if(thread[i].isAlive()) thread[i].fetcher.setExit(true) ;
    }
  }

  static public class FetcherThread extends Thread {
    DistributedHttpFetcher fetcher ;

    public FetcherThread(DistributedHttpFetcher fetcher) {
      super(fetcher) ;
      this.fetcher = fetcher ;
    }
  }
}