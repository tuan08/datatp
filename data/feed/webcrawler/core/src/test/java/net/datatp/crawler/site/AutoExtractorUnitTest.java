package net.datatp.crawler.site;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.datatp.crawler.site.analysis.WebPageAnalyzer;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataContext;
import net.datatp.xhtml.util.WDataHttpFetcher;

public class AutoExtractorUnitTest {
  @Test
  public void testVietnam() throws Exception {
    SiteContextManager manager = new SiteContextManager();
    manager.add(
        new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3).
        setExtractConfig(ExtractConfig.article())
    );
    manager.add(
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 3).
      setExtractConfig(ExtractConfig.article())
    ); 
    
    SiteContext vnexpressCtx = manager.getSiteContext("http://vnexpress.net");
    Assert.assertNotNull(vnexpressCtx);
    
    WebPageAnalyzer vnexpressWebpageAnalyzer = vnexpressCtx.getWebPageAnalyzer();
    
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wdata = fetcher.fetch(
      "Những quy định nổi bật có hiệu lực từ 1/8", 
      "http://vnexpress.net/tin-tuc/thoi-su/giao-thong/nhung-quy-dinh-noi-bat-co-hieu-luc-tu-1-8-3445254.html");
    WDataContext wdataContext = new WDataContext(wdata);
    List<ExtractEntity> extracts  = vnexpressWebpageAnalyzer.extract(wdataContext);
    System.out.println(ExtractEntity.toString(extracts));
  }
  
  @Test
  public void testUS() throws Exception {
    SiteContextManager manager = new SiteContextManager();
    manager.add(
        new SiteConfig("US", "edition.cnn.com", "http://edition.cnn.com", 3).
        setExtractConfig(ExtractConfig.article())
    );
    
    SiteContext cnnCtx = manager.getSiteContext("http://edition.cnn.com");
    Assert.assertNotNull(cnnCtx);
    
    WebPageAnalyzer vnexpressWebpageAnalyzer = cnnCtx.getWebPageAnalyzer();
    
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wdata = fetcher.fetch(
      "Clinton's FBI investigation: What you need to know",
      "http://edition.cnn.com/2016/10/30/politics/clinton-fbi-investigation-comey/index.html");
    WDataContext wdataContext = new WDataContext(wdata);
    List<ExtractEntity> extracts  = vnexpressWebpageAnalyzer.extract(wdataContext);
    System.out.println(ExtractEntity.toString(extracts));
  }
}
