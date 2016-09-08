package net.datatp.crawler.distributed.fetcher;

import java.util.ArrayList;

import net.datatp.channel.ChannelGateway;
import net.datatp.crawler.fetcher.URLFetcher;
import net.datatp.crawler.fetcher.SiteSessionManager;
import net.datatp.crawler.fetcher.URLFetchQueue;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.XDoc;
/**
 * Author: Tuan Nguyen
 *         tuan08@gmail.com
 * Apr 14, 2010
 */
public class DistributedURLFetcher extends URLFetcher {
  private URLFetchQueue urldatumFetchQueue;
  private ChannelGateway     xDocGateway;
  private ChannelGateway     urlFetchCommitGateway;

  public DistributedURLFetcher(String name,
                               SiteContextManager manager,
                               SiteSessionManager siteSessionManager, 
                               URLFetchQueue urldatumFetchQueue,
                               ChannelGateway     urlFetchCommitGateway,
                               ChannelGateway     xDocGateway,
                               FetchDataProcessor fetchDataProcessor) {
    super(name, manager, siteSessionManager, fetchDataProcessor);
    this.urldatumFetchQueue    = urldatumFetchQueue ;
    this.urlFetchCommitGateway = urlFetchCommitGateway;
    this.xDocGateway           = xDocGateway;
  }

  @Override
  protected void onCommit(ArrayList<URLDatum> holder) throws Exception  {
    urlFetchCommitGateway.send(holder);
  }
  
  @Override
  protected void onCommit(XDoc xDoc) throws Exception {
    xDocGateway.send(xDoc);
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