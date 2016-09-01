package net.datatp.crawler.site;

import org.junit.Test;

import net.datatp.crawler.site.analysis.SiteStructureAnalyzer;
import net.datatp.crawler.site.analysis.SiteStructureAnalyzerService;
import net.datatp.util.dataformat.DataSerializer;

public class SiteStructureAnalyzerUnitTest {
  @Test
  public void testVnexpress() throws Exception {
    SiteConfig siteConfig = new SiteConfig("default", "vnexpress.net", "http://vnexpress.net", 3);
    siteConfig.setCrawlSubDomain(true);
    WebPageTypePattern ignoreUrlPattern = new WebPageTypePattern(WebPageType.ignore, ".*utm_campaign.*");
    WebPageTypePattern detailUrlPattern = new WebPageTypePattern(WebPageType.detail, ".*\\-\\d*.html");
    siteConfig.setWebPageTypePatterns(detailUrlPattern, ignoreUrlPattern);
    
    SiteStructureAnalyzerService service = new SiteStructureAnalyzerService(10 * 60 * 1000);
    SiteStructureAnalyzer siteAnalyzer = service.newSiteStructureAnalyzer(siteConfig, 30);

    siteAnalyzer.waitForAnalyseTermination(60000);
    
    siteAnalyzer.getSiteStructure().getUrlSiteStructure().dump(System.out);
    System.out.println(DataSerializer.JSON.toString(siteAnalyzer.getSiteStructure()));
  }
  
  @Test
  public void testOtofun() throws Exception {
    SiteConfig siteConfig = new SiteConfig("default", "otofun.net", "https://www.otofun.net/forums/", 3);
    siteConfig.setCrawlSubDomain(true);
    WebPageTypePattern ignoreUrlPattern = new WebPageTypePattern(WebPageType.ignore, ".*/(posts|members|search)/.*");
    WebPageTypePattern detailUrlPattern = new WebPageTypePattern(WebPageType.detail, ".*/threads/.*");
    siteConfig.setWebPageTypePatterns(detailUrlPattern, ignoreUrlPattern);
    
    SiteStructureAnalyzer siteAnalyzer = new SiteStructureAnalyzer(siteConfig, 30);
    siteAnalyzer.run();
    siteAnalyzer.waitForAnalyseTermination(60000);
    siteAnalyzer.getSiteStructure().getUrlSiteStructure().dump(System.out);
  }
}