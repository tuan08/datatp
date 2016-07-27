package net.datatp.crawler.distributed.processor;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;

import net.datatp.channel.ChannelGateway;
import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.processor.FetchDataProcessor;
import net.datatp.crawler.processor.URLExtractor;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.WData;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataProcessorImpl extends FetchDataProcessor {
  @Autowired
  @Qualifier("WPageDataGateway")
  private ChannelGateway wPageDataGateway ;

  @Autowired
  @Qualifier("URLFetchCommitGateway")
  private ChannelGateway urlFetchCommitGateway ;

  
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
  protected void onSave(WData wpData) throws Exception {
    wPageDataGateway.send(wpData) ;
  }
  
  @JmsListener(destination = "crawler.fetchdata")
  public void process(FetchData fdata) {
    super.process(fdata);
  }
}