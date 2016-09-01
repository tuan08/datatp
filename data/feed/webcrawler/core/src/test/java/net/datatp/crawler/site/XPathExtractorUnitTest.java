package net.datatp.crawler.site;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.datatp.crawler.site.ExtractConfig.ExtractType;
import net.datatp.crawler.site.ExtractConfig.XPathPattern;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataExtractContext;
import net.datatp.xhtml.util.WDataHttpFetcher;

public class XPathExtractorUnitTest {
  @Test
  public void test() throws Exception {
    SiteContextManager manager = new SiteContextManager();
    SiteConfig config = new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3);
    ExtractConfig extractConfig = new ExtractConfig("article", ExtractType.article);
    XPathPattern[] xpathPattern = {
      new XPathPattern("title", ".main_content_detail .title_news"),
      new XPathPattern("description", ".main_content_detail .short_intro"),
      new XPathPattern("content", ".main_content_detail .fck_detail")
    };
    extractConfig.setExtractXPath(xpathPattern);
    config.setExtractConfig(extractConfig);
    manager.add(config);
    
    SiteContext vnexpressCtx = manager.getSiteContext("http://vnexpress.net");
    Assert.assertNotNull(vnexpressCtx);
    
    SiteExtractor vnexpressExtractor = vnexpressCtx.getSiteExtractor();
    
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wdata = fetcher.fetch(
        "Những quy định nổi bật có hiệu lực từ 1/8", 
        "http://vnexpress.net/tin-tuc/thoi-su/giao-thong/nhung-quy-dinh-noi-bat-co-hieu-luc-tu-1-8-3445254.html");
    WDataExtractContext wdataContext = new WDataExtractContext(wdata);
    List<ExtractEntity> extracts  = vnexpressExtractor.extract(wdataContext);
    System.out.println(ExtractEntity.toString(extracts));
  }
}
