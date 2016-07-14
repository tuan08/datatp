package net.datatp.webcrawler.fetcher.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.channel.ChannelGateway;
import net.datatp.webcrawler.ErrorCode;
import net.datatp.webcrawler.ResponseCode;
import net.datatp.webcrawler.fetcher.FetchData;
import net.datatp.webcrawler.fetcher.Fetcher;
import net.datatp.webcrawler.fetcher.model.HttpFetcherInfo;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.webcrawler.site.URLContext;
import net.datatp.webcrawler.urldb.URLDatum;
/**
 * Author: Tuan Nguyen
 *         tuan08@gmail.com
 * Apr 14, 2010
 */
public class HttpFetcher implements Fetcher, Runnable {
  private static final Logger logger = LoggerFactory.getLogger(HttpFetcher.class);

  private SiteContextManager  manager;
  private URLDatumFetchQueue  urldatumFetchQueue;
  private ChannelGateway      fetchDataGateway;

  private SiteSessionManager  siteSessionManager;
  private HttpFetcherInfo     fetcherInfo ;
  private boolean             exit        = false;

  public HttpFetcher(String name,
                     SiteContextManager manager,
                     SiteSessionManager siteSessionManager, 
                     URLDatumFetchQueue urldatumFetchQueue, 
                     ChannelGateway     fetchDataGateway) {
    this.manager = manager ;
    this.urldatumFetchQueue = urldatumFetchQueue ;
    this.fetchDataGateway = fetchDataGateway ;
    this.siteSessionManager = siteSessionManager ;
    this.fetcherInfo = new HttpFetcherInfo(name);
  }

  public HttpFetcherInfo getFetcherInfo() { return fetcherInfo ; } 

  public SiteContextManager getSiteConfigManager() { return manager ; }

  public SiteSessionManager getSiteSessionManager() { return siteSessionManager ; }

  public void fetch(URLDatum datum) throws Exception {
    FetchData fdata = doFetch(datum) ;
    if(fdata != null) fetchDataGateway.send(fdata) ;
  }

  public FetchData doFetch(URLDatum datum) {
    URLContext context = null; 
    try {
      context = manager.getURLContext(datum.getOriginalUrlAsString());
    } catch(Exception ex) {
      ex.printStackTrace();
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_GET) ;
      fetcherInfo.log(datum) ;
      return new FetchData(datum) ;
    }
    if(context == null) {
      datum.setLastErrorCode(ErrorCode.ERROR_DB_CONFIG_NOT_FOUND) ;
      fetcherInfo.log(datum) ;
      return new FetchData(datum) ;
    }

    datum.setLastResponseCode(ResponseCode.NONE) ;
    datum.setLastErrorCode(ErrorCode.ERROR_TYPE_NONE) ;
    SiteSessions sessions = siteSessionManager.getSiteSession(context.getSiteContext()) ;
    SiteSession session = sessions.next() ;
    if(session.isLocked()) {
      try {
        urldatumFetchQueue.addBusy(datum) ;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return null ;
    }
    FetchData fdata = new FetchData(datum) ;
    session.fetch(fdata, context) ;
    fetcherInfo.log(datum) ;
    return fdata ;
  }

  public void setExit(boolean b) { this.exit = b ; }

  public void run() {
    try {
      while(!exit) {
        URLDatum urldatum = urldatumFetchQueue.poll(1000) ;
        if(urldatum != null) fetch(urldatum) ;
      }
    } catch(Throwable ex) {
      logger.error("Error when handling the fetched request", ex) ;
    }
  }
}