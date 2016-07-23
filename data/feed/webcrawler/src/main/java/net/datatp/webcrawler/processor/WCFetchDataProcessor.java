package net.datatp.webcrawler.processor;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;

import net.datatp.channel.ChannelGateway;
import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.processor.FetchDataProcessor;
import net.datatp.http.crawler.processor.URLExtractor;
import net.datatp.http.crawler.processor.metric.ProcessMetric;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.xhtml.XhtmlDocument;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WCFetchDataProcessor extends FetchDataProcessor {
  @Autowired
  @Qualifier("XHTMLDataGateway")
  private ChannelGateway xhtmlDataGateway ;

  @Autowired
  @Qualifier("URLFetchCommitGateway")
  private ChannelGateway urlFetchCommitGateway ;

  private ProcessMetric metric = new ProcessMetric() ;
  
  public ProcessMetric getProcessInfo() { return this.metric; }
  
  @Autowired
  public void setURLExtractor(URLExtractor urlExtractor) {
    this.urlExtractor = urlExtractor;
  }
  
  @Autowired
  public void setSiteContextManager(SiteContextManager manager) {
    this.siteContextManager = manager;
  }
  
  @Override
  protected void onSave(ArrayList<URLDatum> holder) throws Exception {
    urlFetchCommitGateway.send(holder) ;
  }

  @Override
  protected void onSave(XhtmlDocument xdoc) throws Exception {
    xhtmlDataGateway.send(xdoc) ;
  }
  
  @JmsListener(destination = "crawler.fetchdata")
  public void process(FetchData fdata) {
    super.process(fdata);
  }
}