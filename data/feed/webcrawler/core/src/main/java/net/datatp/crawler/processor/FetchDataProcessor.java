package net.datatp.crawler.processor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.fetcher.FetchData;
import net.datatp.crawler.processor.metric.ProcessMetric;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class FetchDataProcessor {
  private static final Logger logger = LoggerFactory.getLogger(FetchDataProcessor.class);

  protected URLExtractor urlExtractor ;
  
  protected SiteContextManager siteContextManager ;

  private ProcessMetric metric = new ProcessMetric() ;

  public ProcessMetric getProcessMetric() { return metric; }

  abstract protected void onSave(ArrayList<URLDatum> urlDatatums) throws Exception;
  abstract protected void onSave(WData wPageData) throws Exception;
  
  public void process(FetchData fdata) {
    metric.incrProcessCount() ;
    final long start = System.currentTimeMillis() ;
    URLDatum urlDatum = fdata.getURLDatum() ;
    byte[] data = fdata.getData();
    WData xdoc = new WData(urlDatum.getOriginalUrl(), urlDatum.getAnchorText(), (byte[])null) ;
    
    try {
      if(data == null) {
        ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
        urlList.add(fdata.getURLDatum()) ;
        onSave(urlList) ;
        return ;
      }

      Charset charset = EncodingDetector.INSTANCE.detect(data, data.length);
      String xhtml = new String(data, charset);
      xdoc.setData(xhtml);
      XPathStructure xpathStructure = new XPathStructure(xdoc.createJsoupDocument());
      
      URLContext context =  siteContextManager.getURLContext(fdata.getURLDatum().getFetchUrl()) ;

      Map<String, URLDatum> urls = urlExtractor.extract(fdata.getURLDatum(), context, xdoc, xpathStructure) ;
      metric.addSumHtmlProcessTime(System.currentTimeMillis() - start) ;

      ArrayList<URLDatum> urlList = new ArrayList<URLDatum>() ;
      urlList.add(urlDatum);
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