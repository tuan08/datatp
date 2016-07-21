package net.datatp.webcrawler.process;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;

import net.datatp.channel.ChannelGateway;
import net.datatp.http.crawler.URLExtractor;
import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.site.URLContext;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.webcrawler.site.WebCrawlerSiteContextManager;
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
  private WebCrawlerSiteContextManager siteConfigManager ;

  @Autowired
  @Qualifier("XHTMLDataGateway")
  private ChannelGateway xhtmlDataGateway ;

  @Autowired
  @Qualifier("URLFetchCommitGateway")
  private ChannelGateway urlFetchCommitGateway ;

  private DataProcessInfo info = new DataProcessInfo() ;

  public DataProcessInfo getDataProcessInfo() { return this.info; }

  @JmsListener(destination = "crawler.fetchdata")
  public void process(FetchData fdata) {
    info.incrProcessCount() ;
    final long start = System.currentTimeMillis() ;
    URLDatum urldatum = fdata.getURLDatum() ;
    byte[] data = fdata.getData();
    XhtmlDocument xdoc = new XhtmlDocument(urldatum.getOriginalUrl(), urldatum.getAnchorText(), null) ;
    if(data == null) {
      ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
      urlList.add(fdata.getURLDatum()) ;
      urlFetchCommitGateway.send(urlList) ;
      return ;
    }
    try {
      if(data != null) {
        Charset charset = EncodingDetector.INSTANCE.detect(data, data.length);
        String xhtml = new String(data, charset);
        xdoc.setXhtml(xhtml);
      }
      
      TDocument  tdoc = 
          new TDocument(urldatum.getAnchorTextAsString(), urldatum.getOriginalUrlAsString(), xdoc.getXhtml()) ;
      URLContext context =  siteConfigManager.getURLContext(fdata.getURLDatum().getFetchUrl()) ;

      Map<String, URLDatum> urls = urlExtractor.extract(fdata.getURLDatum(), context, tdoc) ;
      info.addSumHtmlProcessTime(System.currentTimeMillis() - start) ;

      ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
      urlList.add(fdata.getURLDatum()) ;
      urlList.addAll(urls.values()) ;

      urlFetchCommitGateway.send(urlList) ;
      xhtmlDataGateway.send(xdoc) ;
    } catch(Exception ex) {
      ex.printStackTrace() ;
      logger.error("Cannot process HtmlDocument: " + xdoc.getUrl()) ;
    }
    info.addSumProcessTime(System.currentTimeMillis() - start) ;
  }
}