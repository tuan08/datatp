package net.datatp.crawler.processor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.fetcher.FetchContext;
import net.datatp.crawler.processor.metric.ProcessMetric;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.WDataContext;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class FetchDataProcessor {
  private static final Logger logger = LoggerFactory.getLogger(FetchDataProcessor.class);

  protected URLExtractor urlExtractor ;
  
  private ProcessMetric metric = new ProcessMetric() ;
  
  private FetchProcessorPlugin[] plugin = {
    new FetchErrorAnalyzerPlugin(), new FetchContentExtractorPlugin()
  };

  public FetchDataProcessor() {}
  
  public FetchDataProcessor(URLExtractor urlExtractor) {
    this.urlExtractor = urlExtractor;
  }
  
  public ProcessMetric getProcessMetric() { return metric; }

  public void process(FetchContext fetchCtx) {
    metric.incrProcessCount() ;
    final long start = System.currentTimeMillis() ;
    URLDatum urlDatum = fetchCtx.getURLContext().getURLDatum() ;
    WDataContext wDataCtx = null;
    byte[] data = fetchCtx.getData();
    
    try {
      if(data != null) {
        Charset charset = EncodingDetector.INSTANCE.detect(data, data.length);
        String xhtml = new String(data, charset);
        WData wdata = new WData(urlDatum.getOriginalUrl(), urlDatum.getAnchorText(), xhtml) ;
        wDataCtx = new WDataContext(wdata);
      }
      metric.addSumHtmlProcessTime(System.currentTimeMillis() - start) ;
      processPlugins(fetchCtx, wDataCtx);
      
      processUrls(fetchCtx, wDataCtx);
    } catch(Exception ex) {
      ex.printStackTrace() ;
      logger.error("Cannot process : " + urlDatum.getOriginalUrl()) ;
    }
    metric.addSumProcessTime(System.currentTimeMillis() - start) ;
  }
  
  private void processUrls(FetchContext fetchCtx, WDataContext wDataCtx) throws Exception {
    URLDatum urlDatum = fetchCtx.getURLContext().getURLDatum();
    ArrayList<URLDatum> commitUrls = new ArrayList<URLDatum>() ;
    commitUrls.add(urlDatum);
    if(wDataCtx != null) {
      Map<String, URLDatum> urls = urlExtractor.extract(fetchCtx.getURLContext(), wDataCtx) ;
      commitUrls.addAll(urls.values()) ;
    }
    fetchCtx.getXDocMapper().setUrl(fetchCtx.getURLContext().getURLDatum().getOriginalUrl());
    fetchCtx.getXDocMapper().setMD5Id(fetchCtx.getURLContext().getURLDatum().getId());
    fetchCtx.setCommitURLs(commitUrls);
  }
  
  private void processPlugins(FetchContext fetchCtx, WDataContext wdataCtx) throws Exception {
    for(FetchProcessorPlugin sel : plugin) {
      sel.process(fetchCtx, wdataCtx);
    }
  }
}