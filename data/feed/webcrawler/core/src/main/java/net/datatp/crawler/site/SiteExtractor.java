package net.datatp.crawler.site;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataExtractContext;
import net.datatp.xhtml.extract.WDataExtractor;

public class SiteExtractor {
  private AutoExtractor[] autoPageExtractors;
  private AutoExtractor[] xpathPageExtractors;
  
  public SiteExtractor(SiteConfig siteConfig, AutoWDataExtractors extractors) {
    List<AutoExtractor> autoHolder  = new ArrayList<>();
    List<AutoExtractor> xpathHolder = new ArrayList<>();
    
    ExtractConfig[] extractConfigs = siteConfig.getExtractConfig();
    if(extractConfigs != null) {
      for(ExtractConfig extractConfig : extractConfigs) {
        if(extractConfig.getExtractXPath() != null) {
        }
        
        if(extractConfig.getExtractType() != null) {
          ExtractConfig.ExtractType extractType  = extractConfig.getExtractType() ;
          WDataExtractor extractor = extractors.getExtractor(extractType);
          AutoExtractor pageExtractor = new AutoExtractor(extractConfig, extractor);
          autoHolder.add(pageExtractor);
        }
      }
    }
    autoPageExtractors  = autoHolder.toArray(new AutoExtractor[autoHolder.size()]);
    xpathPageExtractors = xpathHolder.toArray(new AutoExtractor[xpathHolder.size()]);
  }
  
  public List<ExtractEntity> extract(WDataExtractContext context) {
    WData wdata = context.getWdata();
    List<ExtractEntity> extractEntities = new ArrayList<>() ;
    if(xpathPageExtractors.length > 0) {
      for(AutoExtractor pExtractor : xpathPageExtractors) {
        if(pExtractor.matches(wdata)) {
          ExtractEntity entity = pExtractor.extractEntity(context);
          if(entity != null) extractEntities.add(entity);
        }
      }
    }
    if(autoPageExtractors.length > 0) {
      for(AutoExtractor pExtractor : autoPageExtractors) {
        if(pExtractor.matches(wdata)) {
          ExtractEntity entity = pExtractor.extractEntity(context);
          if(entity != null) extractEntities.add(entity);
        }
      }
    }
    return extractEntities;
  }
  
  static public class BaseExtractor {
    private ExtractConfig extractConfig;
    private Pattern[]     pattern;
    
    public BaseExtractor(ExtractConfig extractConfig) {
      this.extractConfig = extractConfig;
      String[] patternExp = extractConfig.getMatchPattern();
      if(patternExp != null) {
        pattern = new Pattern[patternExp.length];
        for(int i = 0; i < pattern.length; i++) {
          pattern[i] = Pattern.compile(patternExp[i].trim());
        }
      } else {
        pattern = new Pattern[0];
      }
    }
    
    public boolean matches(WData wdata) {
      if(extractConfig.getMatchType() == ExtractConfig.MatchType.any) {
        return true;
      } else if(extractConfig.getMatchType() == ExtractConfig.MatchType.url) {
        return matchUrl(wdata);
      } else {
        return matchTitle(wdata);
      }
    }
    
    boolean matchUrl(WData wdata) {
      String url = wdata.getUrl();
      for(int i = 0; i < pattern.length; i++) {
        if(pattern[i].matcher(url).matches()) return true;
      }
      return false;
    }
    
    boolean matchTitle(WData wdata) {
      String title = wdata.getAnchorText();
      for(int i = 0; i < pattern.length; i++) {
        if(pattern[i].matcher(title).matches()) return true;
      }
      return false;
    }
  }
  
  static public class AutoExtractor extends BaseExtractor {
    private WDataExtractor extractor;
    
    public AutoExtractor(ExtractConfig extractConfig, WDataExtractor extractor) {
      super(extractConfig);
      this.extractor     = extractor;
    }
    
    public ExtractEntity extractEntity(WDataExtractContext context) {
      return extractor.extractEntity(context);
    }
  }
  
  static public class XPathExtractor extends BaseExtractor {
    
    public XPathExtractor(ExtractConfig extractConfig) {
      super(extractConfig);
      extractConfig.getExtractXPath();
    }
    
    public ExtractEntity extractEntity(WDataExtractContext context) {
      return null;
    }
  }
}
