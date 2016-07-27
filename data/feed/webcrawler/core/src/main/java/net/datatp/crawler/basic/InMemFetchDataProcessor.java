package net.datatp.crawler.basic;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.processor.URLExtractor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.WData;

public class InMemFetchDataProcessor extends FetchDataProcessor {
  
  private BlockingQueue<URLDatum>  urlCommitQueue;
  private BlockingQueue<WData> wPageDataQueue;
  
  public InMemFetchDataProcessor(SiteContextManager manager, 
                                 URLExtractor urlExtractor, 
                                 BlockingQueue<URLDatum>  urlCommitQueue,
                                 BlockingQueue<WData> wPageDataQueue) {
    this.siteContextManager = manager;
    this.urlExtractor       = urlExtractor;
    this.urlCommitQueue     = urlCommitQueue;
    this.wPageDataQueue     = wPageDataQueue;
  }
  
  @Override
  protected void onSave(ArrayList<URLDatum> holder) throws Exception {
    for(int i = 0; i < holder.size(); i++) {
      URLDatum urlDatum = holder.get(i);
      urlCommitQueue.offer(urlDatum, 5, TimeUnit.SECONDS);
    }
  }

  @Override
  protected void onSave(WData wpData) throws Exception {
    wPageDataQueue.offer(wpData, 5, TimeUnit.SECONDS);
  }
}
