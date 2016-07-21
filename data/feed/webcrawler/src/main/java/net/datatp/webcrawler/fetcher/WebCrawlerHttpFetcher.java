package net.datatp.webcrawler.fetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.channel.ChannelGateway;
import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.fetcher.HttpFetcher;
import net.datatp.http.crawler.fetcher.SiteSessionManager;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;
/**
 * Author: Tuan Nguyen
 *         tuan08@gmail.com
 * Apr 14, 2010
 */
public class WebCrawlerHttpFetcher extends HttpFetcher {
  private static final Logger logger = LoggerFactory.getLogger(WebCrawlerHttpFetcher.class);

  private URLDatumFetchQueue  urldatumFetchQueue;
  private ChannelGateway      fetchDataGateway;

  public WebCrawlerHttpFetcher(String name,
                     SiteContextManager manager,
                     SiteSessionManager siteSessionManager, 
                     URLDatumFetchQueue urldatumFetchQueue, 
                     ChannelGateway     fetchDataGateway) {
    super(name, manager, siteSessionManager);
    this.urldatumFetchQueue = urldatumFetchQueue ;
    this.fetchDataGateway = fetchDataGateway ;
  }

  @Override
  protected void onCommit(FetchData fdata) throws Exception {
    fetchDataGateway.send(fdata) ;
  }
  
  @Override
  protected void onDelay(URLDatum urlDatum) throws InterruptedException {
    urldatumFetchQueue.addBusy(urlDatum) ;
  }
  
  /**
   * The next url to fetch
   * @return
   * @throws Exception
   */
  protected URLDatum nextURLDatum(long maxWaitTime) throws Exception {
    URLDatum urldatum = urldatumFetchQueue.poll(1000) ;
    return urldatum;
  }
}