package net.datatp.crawler.distributed.fetcher;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.fetcher.HttpFetcher;
import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
/**
 * Author: Tuan Nguyen
 *         tuan08@gmail.com
 * Apr 14, 2010
 */
public class DistributedHttpFetcher extends HttpFetcher {
  private URLDatumFetchQueue  urldatumFetchQueue;
  private FetchDataProcessor fetchDataProcessor;

  public DistributedHttpFetcher(String name,
                     SiteContextManager manager,
                     SiteSessionManager siteSessionManager, 
                     URLDatumFetchQueue urldatumFetchQueue, 
                     FetchDataProcessor fetchDataProcessor) {
    super(name, manager, siteSessionManager);
    this.urldatumFetchQueue = urldatumFetchQueue ;
    this.fetchDataProcessor = fetchDataProcessor ;
  }

  @Override
  protected void onProcess(FetchData fdata) throws Exception {
    fetchDataProcessor.process(fdata);
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