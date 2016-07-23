package net.datatp.http.crawler.processor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.http.crawler.fetcher.FetchData;
import net.datatp.http.crawler.processor.metric.ProcessMetric;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.site.URLContext;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class FetchDataProcessor {
  private static final Logger logger = LoggerFactory.getLogger(FetchDataProcessor.class);

  protected URLExtractor urlExtractor ;
  
  protected SiteContextManager siteContextManager ;

  private ProcessMetric metric = new ProcessMetric() ;

  public ProcessMetric getProcessInfo() { return this.metric; }

  abstract protected void onSave(ArrayList<URLDatum> holder) throws Exception;
  abstract protected void onSave(XhtmlDocument doc) throws Exception;
  
  public void process(FetchData fdata) {
    metric.incrProcessCount() ;
    final long start = System.currentTimeMillis() ;
    URLDatum urldatum = fdata.getURLDatum() ;
    byte[] data = fdata.getData();
    XhtmlDocument xdoc = new XhtmlDocument(urldatum.getOriginalUrl(), urldatum.getAnchorText(), null) ;
    
    try {
      if(data == null) {
        ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
        urlList.add(fdata.getURLDatum()) ;
        onSave(urlList) ;
        return ;
      }

      Charset charset = EncodingDetector.INSTANCE.detect(data, data.length);
      String xhtml = new String(data, charset);
      xdoc.setXhtml(xhtml);
      XPathStructure xpathStructure = new XPathStructure(xdoc.createJsoupDocument());
      
      
//      TDocument  tdoc = 
//          new TDocument(urldatum.getAnchorTextAsString(), urldatum.getOriginalUrlAsString(), xdoc.getXhtml()) ;
      URLContext context =  siteContextManager.getURLContext(fdata.getURLDatum().getFetchUrl()) ;

      Map<String, URLDatum> urls = urlExtractor.extract(fdata.getURLDatum(), context, xdoc, xpathStructure) ;
      metric.addSumHtmlProcessTime(System.currentTimeMillis() - start) ;

      ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
      urlList.add(fdata.getURLDatum()) ;
      urlList.addAll(urls.values()) ;

      onSave(urlList) ;
      onSave(xdoc) ;
    } catch(Exception ex) {
      ex.printStackTrace() ;
      logger.error("Cannot process HtmlDocument: " + xdoc.getUrl()) ;
    }
    metric.addSumProcessTime(System.currentTimeMillis() - start) ;
  }
}