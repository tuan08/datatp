package net.datatp.crawler.site;

import java.util.ArrayList;
import java.util.List;

import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.WDataContext;
import net.datatp.xhtml.xpath.WDataExtractor;

public class SiteExtractor {
  private PageExtractor[] autoPageExtractors;
  private PageExtractor[] xpathPageExtractors;
  
  public SiteExtractor(SiteConfig siteConfig, AutoWDataExtractors extractors) {
    List<PageExtractor> autoHolder  = new ArrayList<>();
    List<PageExtractor> xpathHolder = new ArrayList<>();
    
    for(ExtractConfig extractConfig : siteConfig.getExtractConfig()) {
      if(extractConfig.getExtractXPath() != null) {
        
      }
      
      if(extractConfig.getExtractAuto() != null) {
        for(ExtractConfig.ExtractAuto extractAuto  : extractConfig.getExtractAuto()) {
          WDataExtractor extractor = extractors.getExtractor(extractAuto);
          PageExtractor pageExtractor = new PageExtractor(extractConfig, extractor);
          autoHolder.add(pageExtractor);
        }
      }
    }
    
    autoPageExtractors  = autoHolder.toArray(new PageExtractor[autoHolder.size()]);
    xpathPageExtractors = autoHolder.toArray(new PageExtractor[xpathHolder.size()]);
  }
  
  public void extract(WDataContext context) {
    WData wdata = context.getWdata();
    int extractCount = 0;
    if(xpathPageExtractors.length > 0) {
      for(PageExtractor pExtractor : xpathPageExtractors) {
        if(pExtractor.matches(wdata)) {
          extractCount = pExtractor.extract(context);
        }
      }
    }
    if(extractCount == 0 && autoPageExtractors.length > 0) {
      for(PageExtractor pExtractor : autoPageExtractors) {
        if(pExtractor.matches(wdata)) {
          extractCount = pExtractor.extract(context);
        }
      }
    }
  }
  
  static public class PageExtractor {
    private WDataExtractor extractor;
    private PageMatcher    pageMatcher;
    
    public PageExtractor(ExtractConfig extractConfig, WDataExtractor extractor) {
      this.extractor     = extractor;
      this.pageMatcher   = new PageMatcher(extractConfig.getMatcher());
    }
    
    public boolean matches(WData wdata) {
      return pageMatcher.matches(wdata);
    }
    
    public int extract(WDataContext context) {
      return extractor.extract(context);
    }
  }
  
  static public class PageMatcher {
    ExtractConfig.Matcher matcherConfig;
    
    public PageMatcher(ExtractConfig.Matcher matcherConfig) {
      this.matcherConfig = matcherConfig;
    }
    
    public boolean matches(WData wdata) {
      if(matcherConfig != null) {
        return false;
      } else {
        return true;
      }
    }
  }
}
