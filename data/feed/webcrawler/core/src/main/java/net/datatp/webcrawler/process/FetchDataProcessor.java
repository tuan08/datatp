package net.datatp.webcrawler.process;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import net.datatp.channel.ChannelGateway;
import net.datatp.webcrawler.fetcher.FetchData;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.webcrawler.urldb.URLContext;
import net.datatp.webcrawler.urldb.URLDatum;
import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.dom.TDocument;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataProcessor {
  private static final Logger logger = LoggerFactory.getLogger(FetchDataProcessor.class);

  @Autowired
  private URLExtractor urlExtractor ;
  @Autowired
  private SiteContextManager siteConfigManager ;

  @Autowired
  @Qualifier("XHTMLDataGateway")
  private ChannelGateway xhtmlDataGateway ;

  @Autowired
  @Qualifier("URLFetchCommitGateway")
  private ChannelGateway urldatumFetchGateway ;

  private DataProcessInfo info = new DataProcessInfo() ;

  public DataProcessInfo getDataProcessInfo() { return this.info; }

  public void process(FetchData fdata) {
    info.incrProcessCount() ;
    final long start = System.currentTimeMillis() ;
    XhtmlDocument xdoc = fdata.getDocument() ;
    if(xdoc == null) {
      ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
      urlList.add(fdata.getURLDatum()) ;
      urldatumFetchGateway.send(urlList) ;
      return ;
    }
    try {
      URLDatum urldatum = fdata.getURLDatum() ;
      TDocument  tdoc = 
          new TDocument(urldatum.getAnchorTextAsString(), urldatum.getOriginalUrlAsString(), xdoc.getXhtml()) ;
      URLContext context =  siteConfigManager.getURLContext(fdata.getURLDatum().getFetchUrl()) ;

      Map<String, URLDatum> urls = urlExtractor.extract(fdata.getURLDatum(), context, tdoc) ;
      info.addSumHtmlProcessTime(System.currentTimeMillis() - start) ;

      ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
      urlList.add(fdata.getURLDatum()) ;
      urlList.addAll(urls.values()) ;

      urldatumFetchGateway.send(urlList) ;
      xhtmlDataGateway.send(fdata.getDocument()) ;
    } catch(Exception ex) {
      ex.printStackTrace() ;
      logger.error("Cannot process HtmlDocument: " + xdoc.getUrl()) ;
    }
    info.addSumProcessTime(System.currentTimeMillis() - start) ;
  }
}