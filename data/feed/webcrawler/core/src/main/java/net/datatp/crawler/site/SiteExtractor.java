package net.datatp.crawler.site;

import java.util.ArrayList;
import java.util.List;

import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.WDataExtractContext;
import net.datatp.xhtml.extract.WDataExtractors;
import net.datatp.xhtml.extract.entity.ExtractEntity;

public class SiteExtractor {
  private PageExtractor[] autoPageExtractors;
  private PageExtractor[] xpathPageExtractors;
  
  public SiteExtractor(SiteConfig siteConfig, AutoWDataExtractors extractors) {
    List<PageExtractor> autoHolder  = new ArrayList<>();
    List<PageExtractor> xpathHolder = new ArrayList<>();
    
    ExtractConfig[] extractConfigs = siteConfig.getExtractConfig();
    if(extractConfigs != null) {
      for(ExtractConfig extractConfig : extractConfigs) {
        if(extractConfig.getExtractXPath() != null) {
        }

        if(extractConfig.getExtractAuto() != null) {
          for(ExtractConfig.ExtractAuto extractAuto  : extractConfig.getExtractAuto()) {
            WDataExtractors extractor = extractors.getExtractor(extractAuto);
            PageExtractor pageExtractor = new PageExtractor(extractConfig, extractor);
            autoHolder.add(pageExtractor);
          }
        }
      }
    }
    autoPageExtractors  = autoHolder.toArray(new PageExtractor[autoHolder.size()]);
    xpathPageExtractors = xpathHolder.toArray(new PageExtractor[xpathHolder.size()]);
  }
  
  public List<ExtractEntity> extract(WDataExtractContext context) {
    WData wdata = context.getWdata();
    List<ExtractEntity> extractResults = null ;
    if(xpathPageExtractors.length > 0) {
      for(PageExtractor pExtractor : xpathPageExtractors) {
        if(pExtractor.matches(wdata)) {
          extractResults = pExtractor.extractEntity(context);
        }
      }
    }
    if(extractResults == null && autoPageExtractors.length > 0) {
      for(PageExtractor pExtractor : autoPageExtractors) {
        if(pExtractor.matches(wdata)) {
          extractResults = pExtractor.extractEntity(context);
        }
      }
    }
    return extractResults;
  }
  
  static public class PageExtractor {
    private WDataExtractors extractor;
    private PageMatcher    pageMatcher;
    
    public PageExtractor(ExtractConfig extractConfig, WDataExtractors extractor) {
      this.extractor     = extractor;
      this.pageMatcher   = new PageMatcher(extractConfig);
    }
    
    public boolean matches(WData wdata) { return pageMatcher.matches(wdata); }
    
    public List<ExtractEntity> extractEntity(WDataExtractContext context) {
      return extractor.extractEntity(context);
    }
  }
  
  static public class PageMatcher {
    ExtractConfig extractConfig;
    
    public PageMatcher(ExtractConfig extractConfig) {
      this.extractConfig = extractConfig;
    }
    
    public boolean matches(WData wdata) {
      if(extractConfig.getMatchPattern() == null) {
        return true;
      } else {
        return false;
      }
    }
  }
}
