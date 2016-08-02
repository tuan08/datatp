package net.datatp.crawler.fetcher;

import java.util.ArrayList;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.fetcher.metric.HttpFetcherMetric;
import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.http.HttpClientFactory;
import net.datatp.crawler.http.ResponseCode;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.XDoc;
/**
 * Author: Tuan Nguyen
 *         tuan08@gmail.com
 * Apr 14, 2010
 */
abstract public class HttpFetcher implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(HttpFetcher.class);

  private SiteContextManager  manager;
  private FetchDataProcessor  dataProcessor;
  
  private SiteSessionManager  siteSessionManager;
  private HttpFetcherMetric   fetcherMetric ;
  private CloseableHttpClient httpClient ;
  private boolean             exit        = false;

  public HttpFetcher(String name,
                     SiteContextManager manager,
                     SiteSessionManager siteSessionManager,
                     FetchDataProcessor dataProcessor) {
    this.manager            = manager ;
    this.siteSessionManager = siteSessionManager ;
    this.dataProcessor      = dataProcessor;
    this.fetcherMetric      = new HttpFetcherMetric(name);
    this.httpClient         = HttpClientFactory.createInstance() ;
  }

  public CloseableHttpClient getHttpClient() { return httpClient; }
  
  public HttpFetcherMetric getFetcherMetric() { return fetcherMetric ; } 
  
  public void setExit(boolean b) { this.exit = b ; }

  abstract protected void onCommit(ArrayList<URLDatum> holder) throws Exception ;

  abstract protected void onCommit(XDoc xDoc) throws Exception ;

  
  /**
   * The site is reached the max number of connection, store the url into a queue or database for later reprocess.
   * @param urlDatum
   * @throws InterruptedException
   */
  abstract protected void onDelay(URLDatum urlDatum) throws InterruptedException;
  
  /**
   * The next url to fetch
   * @return
   * @throws Exception
   */
  abstract protected URLDatum nextURLDatum(long maxWaitTime) throws Exception;
  
  public void fetch(URLDatum datum) throws Exception {
    FetchContext fetchCtx = doFetch(datum) ;
    if(fetchCtx != null) {
      dataProcessor.process(fetchCtx);
      onCommit(fetchCtx.getCommitURLs());
      onCommit(fetchCtx.getXDocMapper().getXDoc());
    }
  }

  public FetchContext doFetch(URLDatum datum) {
    URLContext urlContext = null; 
    try {
      urlContext = manager.getURLContext(datum);
    } catch(Exception ex) {
      ex.printStackTrace();
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_GET) ;
      fetcherMetric.log(datum) ;
      return new FetchContext(this, null, urlContext) ;
    }
    if(urlContext == null) {
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_NOT_FOUND) ;
      fetcherMetric.log(datum) ;
      return new FetchContext(this, null, urlContext) ;
    }

    datum.setLastResponseCode(ResponseCode.NONE) ;
    datum.setLastErrorCode(ErrorCode.ERROR_TYPE_NONE) ;
    SiteSessions sessions = siteSessionManager.getSiteSession(urlContext.getSiteContext()) ;
    SiteSession session = sessions.next() ;
    if(session.isLocked()) {
      try {
        onDelay(datum) ;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return null ;
    }
    FetchContext fetchCtx = session.fetch(this, urlContext) ;
    fetcherMetric.log(datum) ;
    return fetchCtx ;
  }

  public void run() {
    exit = false;
    try {
      while(!exit) {
        URLDatum urldatum = nextURLDatum(1000) ;
        if(urldatum != null) fetch(urldatum) ;
      }
    } catch(Throwable ex) {
      logger.error("Error when handling the fetched request", ex) ;
    }
  }
}