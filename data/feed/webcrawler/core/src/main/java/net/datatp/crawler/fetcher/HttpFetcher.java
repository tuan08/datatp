package net.datatp.crawler.fetcher;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.fetcher.metric.HttpFetcherMetric;
import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.http.HttpClientFactory;
import net.datatp.crawler.http.ResponseCode;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;
/**
 * Author: Tuan Nguyen
 *         tuan08@gmail.com
 * Apr 14, 2010
 */
abstract public class HttpFetcher implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(HttpFetcher.class);

  private SiteContextManager  manager;

  private SiteSessionManager  siteSessionManager;
  private HttpFetcherMetric   fetcherMetric ;
  private CloseableHttpClient httpclient ;
  private boolean             exit        = false;

  public HttpFetcher(String name,
                     SiteContextManager manager,
                     SiteSessionManager siteSessionManager) {
    this.manager = manager ;
    this.siteSessionManager = siteSessionManager ;
    this.fetcherMetric = new HttpFetcherMetric(name);
    this.httpclient = HttpClientFactory.createInstance() ;
  }

  public HttpFetcherMetric getFetcherMetric() { return fetcherMetric ; } 
  
  public void setExit(boolean b) { this.exit = b ; }

  /**
   * Save the commit data to an implemented database or queue for later process. 
   * @param fdata
   * @throws Exception
   */
  abstract protected void onProcess(FetchData fdata) throws Exception;
  
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
    FetchData fdata = doFetch(datum) ;
    if(fdata != null) onProcess(fdata) ;
  }

  public FetchData doFetch(URLDatum datum) {
    URLContext context = null; 
    try {
      context = manager.getURLContext(datum.getOriginalUrlAsString());
    } catch(Exception ex) {
      ex.printStackTrace();
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_GET) ;
      fetcherMetric.log(datum) ;
      return new FetchData(datum) ;
    }
    if(context == null) {
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_NOT_FOUND) ;
      fetcherMetric.log(datum) ;
      return new FetchData(datum) ;
    }

    datum.setLastResponseCode(ResponseCode.NONE) ;
    datum.setLastErrorCode(ErrorCode.ERROR_TYPE_NONE) ;
    SiteSessions sessions = siteSessionManager.getSiteSession(context.getSiteContext()) ;
    SiteSession session = sessions.next() ;
    if(session.isLocked()) {
      try {
        onDelay(datum) ;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return null ;
    }
    FetchData fdata = session.fetch(httpclient, datum, context) ;
    fetcherMetric.log(datum) ;
    return fdata ;
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