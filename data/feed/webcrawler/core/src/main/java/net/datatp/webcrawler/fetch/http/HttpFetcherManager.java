package net.datatp.webcrawler.fetch.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.datatp.queue.DataQueue;
import net.datatp.webcrawler.fetch.FetchData;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.webcrawler.urldb.URLDatum;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 14, 2010  
 */
public class HttpFetcherManager  {
  static int MAX_QUEUE_CAPACITY = 250000 ;
  private static final Logger logger = LoggerFactory.getLogger(HttpFetcherManager.class);

  private String name = "HttpFetcherManager" ;

  @Autowired
  private SiteContextManager manager ;

  private URLDatumFetchQueue urldatumFetchQueue = new URLDatumFetchQueue();

  @Autowired
  @Qualifier("FetchDataGateway")
  private DataQueue<FetchData> fetchDataGateway ;

  @Autowired
  private SiteSessionManager siteSessionManager ;

  private FetcherThread[] thread ;
  private int numberOfFetcher = 1 ;

  public String getName() { return name ; }
  public void   setName(String name) { this.name = name ; }

  public List<FetcherInfo> getFetcherInfo() {
    List<FetcherInfo> holder = new ArrayList<FetcherInfo>() ;
    if(thread != null) {
      for(int i = 0; i < thread.length; i++) {
        if(thread[i] != null) {
          holder.add(thread[i].fetcher.getFetcherInfo()) ;
        }
      }
    }
    return holder ;
  }

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


  public void setNumberOfFetcher(int value) { this.numberOfFetcher = value ; }

  public void onInit() {
    thread = new FetcherThread[numberOfFetcher] ;
  }

  public void start() {
    for(int i = 0; i < thread.length; i++) {
      if(thread[i] == null || !thread[i].isAlive()) {
        HttpFetcher fetcher =  new HttpFetcher(manager, siteSessionManager, urldatumFetchQueue, fetchDataGateway) ;
        thread[i] = new FetcherThread(fetcher) ;
        thread[i].setName(name + "-" + i) ;
        thread[i].start() ;
      }
    }
    logger.info("Start HttpFetcherManager " + name) ;
  }

  public void stop() {
    if(thread == null) return ;
    for(int i = 0; i < thread.length; i++) {
      if(thread[i] != null && thread[i].isAlive()) thread[i].fetcher.setExit(true) ;
    }
  }

  static public class FetcherThread extends Thread {
    HttpFetcher fetcher ;

    public FetcherThread(HttpFetcher fetcher) {
      super(fetcher) ;
      this.fetcher = fetcher ;
    }
  }
}