package net.datatp.crawler.site.analysis;

import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.SiteCrawler;
import net.datatp.crawler.site.URLPattern;
import net.datatp.util.URLParser;
import net.datatp.xhtml.extract.WDataExtractContext;

public class SiteStructureAnalyzer extends SiteCrawler {
  private URLStructure urlStructure;
  
  public SiteStructureAnalyzer(String site, String injectUrl, int maxDownload) {
    super(site, injectUrl, maxDownload);
    urlStructure = new URLStructure();
  }
  
  public SiteStructureAnalyzer(SiteConfig sConfig, int maxDownload) {
    super(sConfig, maxDownload);
    urlStructure = new URLStructure();
  }
  
  public URLStructure getURLStructure() { return urlStructure; }
  
  @Override
  public void onWData(WDataExtractContext ctx) {
    URLParser urlParser = ctx.getURLParser();
    URLPattern urlPattern = getSiteContext().matchesURLPattern(urlParser);
    if(urlPattern != null) {
      urlParser.addTag("url:type:" + urlPattern.getType());
    }
    urlStructure.add(urlParser);
    System.out.println("SiteStructureAnalyzer: Fetch" + urlParser.getNormalizeURLAll());
  }
  
}
