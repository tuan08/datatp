package net.datatp.crawler.site;

import org.junit.Test;

import net.datatp.crawler.site.analysis.SiteStructureAnalyzer;

public class SiteStructureAnalyzerUnitTest {
  @Test
  public void testVnexpress() throws Exception {
    SiteConfig siteConfig = new SiteConfig("default", "vnexpress.net", "http://vnexpress.net", 3);
    siteConfig.setCrawlSubDomain(true);
    URLPattern ignoreUrlPattern = new URLPattern(URLPattern.Type.ignore, ".*utm_campaign.*");
    URLPattern detailUrlPattern = new URLPattern(URLPattern.Type.detail, ".*\\-\\d*.html");
    siteConfig.setURLPatterns(detailUrlPattern, ignoreUrlPattern);
    
    SiteStructureAnalyzer siteCrawler = new SiteStructureAnalyzer(siteConfig, 200);
    siteCrawler.crawl();
    
    siteCrawler.getURLStructure().dump(System.out);
  }
  
  @Test
  public void testOtofun() throws Exception {
    SiteConfig siteConfig = new SiteConfig("default", "otofun.net", "https://www.otofun.net/forums/", 3);
    siteConfig.setCrawlSubDomain(true);
    URLPattern ignoreUrlPattern = new URLPattern(URLPattern.Type.ignore, ".*/(posts|members|search)/.*");
    URLPattern detailUrlPattern = new URLPattern(URLPattern.Type.detail, ".*/threads/.*");
    siteConfig.setURLPatterns(detailUrlPattern, ignoreUrlPattern);
    
    SiteStructureAnalyzer siteCrawler = new SiteStructureAnalyzer(siteConfig, 25);
    siteCrawler.crawl();
    
    siteCrawler.getURLStructure().dump(System.out);
  }
}