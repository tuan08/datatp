package net.datatp.crawler.basic;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.processor.URLExtractor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.XhtmlDocument;

public class InMemFetchDataProcessor extends FetchDataProcessor {
  
  private BlockingQueue<URLDatum>      urlCommitQueue;
  private BlockingQueue<XhtmlDocument> xhtmlDocumentQueue;
  
  public InMemFetchDataProcessor(SiteContextManager manager, 
                                 URLExtractor urlExtractor, 
                                 BlockingQueue<URLDatum>  urlCommitQueue,
                                 BlockingQueue<XhtmlDocument> xhtmlDocumentQueue) {
    this.siteContextManager = manager;
    this.urlExtractor       = urlExtractor;
    this.urlCommitQueue     = urlCommitQueue;
    this.xhtmlDocumentQueue = xhtmlDocumentQueue;
  }
  
  @Override
  protected void onSave(ArrayList<URLDatum> holder) throws Exception {
    for(int i = 0; i < holder.size(); i++) {
      URLDatum urlDatum = holder.get(i);
      urlCommitQueue.offer(urlDatum, 5, TimeUnit.SECONDS);
    }
  }

  @Override
  protected void onSave(XhtmlDocument doc) throws Exception {
    xhtmlDocumentQueue.offer(doc, 5, TimeUnit.SECONDS);
  }
}
