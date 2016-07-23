package net.datatp.http.crawler.processor;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.xhtml.XhtmlDocument;

public class InMemFetchDataProcessor extends FetchDataProcessor {
  
  private BlockingQueue<FetchData> dataFetchQueue; 
  private BlockingQueue<URLDatum>  urlCommitQueue;
  
  private boolean                  terminate    = false;
  private ProcessorThread          processorThread;
  
  public InMemFetchDataProcessor(SiteContextManager manager, 
                                 URLExtractor urlExtractor, 
                                 BlockingQueue<FetchData> dataFetchQueue, 
                                 BlockingQueue<URLDatum>  urlCommitQueue) {
    this.siteContextManager = manager;
    this.urlExtractor       = urlExtractor;
    
    this.dataFetchQueue = dataFetchQueue;
    this.urlCommitQueue = urlCommitQueue;
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
    //System.out.println("Process XhtmlDocument " + doc.getUrl());
  }

  public void start() {
    terminate = false;
    processorThread = new ProcessorThread();
    processorThread.start();
  }
  
  public void stop() {
    if(processorThread == null) return;
    terminate = true;
    processorThread.interrupt();
  }
  
  public class ProcessorThread extends Thread {
    public void run() {
      while(!terminate) {
        try {
          FetchData fdata = dataFetchQueue.poll(1, TimeUnit.SECONDS);
          if(fdata != null) process(fdata);
        } catch (InterruptedException e) {
        }
      }
    }
  }
}
