package net.datatp.crawler.site;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.WDataExtract;
import net.datatp.xhtml.extract.WDataExtractContext;
import net.datatp.xhtml.util.WDataHttpFetcher;

public class SiteExtractorUnitTest {
  @Test
  public void test() throws Exception {
    SiteContextManager manager = new SiteContextManager();
    manager.addConfig(
        new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3).
        setExtractConfig(ExtractConfig.article())
    );
    manager.addConfig(
      new SiteConfig("vietnam", "dantri.com.vn", "http://dantri.com.vn", 3).
      setExtractConfig(ExtractConfig.article())
    ); 
    
    SiteContext vnexpressCtx = manager.getSiteContext("http://vnexpress.net");
    Assert.assertNotNull(vnexpressCtx);
    
    SiteExtractor vnexpressExtractor = vnexpressCtx.getSiteExtractor();
    
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wdata = fetcher.fetch(
        "Những quy định nổi bật có hiệu lực từ 1/8", 
        "http://vnexpress.net/tin-tuc/thoi-su/giao-thong/nhung-quy-dinh-noi-bat-co-hieu-luc-tu-1-8-3445254.html");
    WDataExtractContext wdataContext = new WDataExtractContext(wdata);
    List<WDataExtract> extracts  = vnexpressExtractor.extract(wdataContext);
    System.out.println(WDataExtract.format(extracts));
  }
}
