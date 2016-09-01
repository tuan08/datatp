package net.datatp.crawler.site;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.datatp.crawler.site.ExtractConfig.XPathPattern;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.ExtractEntity;
import net.datatp.xhtml.extract.WDataExtractContext;
import net.datatp.xhtml.extract.WDataExtractor;
import net.datatp.xhtml.xpath.FormatTextExtractor;
import net.datatp.xhtml.xpath.NodeCleaner;
import net.datatp.xhtml.xpath.NodeCleanerVisitor;

public class SiteExtractor {
  private AutoWDataExtractors autoWDataExtractors;
  private EntityExtractor[]   entityExtractor;
  
  public SiteExtractor(SiteConfig siteConfig, AutoWDataExtractors autoWDataExtractors) {
    this.autoWDataExtractors = autoWDataExtractors;
    update(siteConfig);
  }
  
  public void update(SiteConfig siteConfig) {
    ExtractConfig[] extractConfigs = siteConfig.getExtractConfig();
    if(extractConfigs != null) {
      this.entityExtractor = new EntityExtractor[extractConfigs.length];
      for(int i = 0; i < extractConfigs.length; i++) {
        entityExtractor[i] = new EntityExtractor(extractConfigs[i], autoWDataExtractors);
      }
    }
  }
  
  public List<ExtractEntity> extract(WDataExtractContext context) {
    List<ExtractEntity> extractEntities = new ArrayList<>() ;
    if(entityExtractor == null) return extractEntities;
    for(int i = 0; i < entityExtractor.length; i++) {
      ExtractEntity entity = entityExtractor[i].extractEntity(context);
      if(entity != null) extractEntities.add(entity);
    }
    return extractEntities;
  }
  
  static public class EntityExtractor {
    private ExtractConfig  extractConfig;
    private Pattern[]      pattern;
    private XPathExtractor xpathExtractor;
    private AutoExtractor  autoExtractor;

    public EntityExtractor(ExtractConfig extractConfig, AutoWDataExtractors extractors) {
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
      
      if(extractConfig.getExtractXPath() != null) {
        xpathExtractor = new XPathExtractor(extractConfig);
      }
      
      if(extractConfig.getExtractType() != null) {
        ExtractConfig.ExtractType extractType  = extractConfig.getExtractType() ;
        WDataExtractor extractor = extractors.getExtractor(extractType);
        autoExtractor = new AutoExtractor(extractConfig, extractor);
      }
    }
    
    public ExtractEntity extractEntity(WDataExtractContext context) {
      WData wdata = context.getWdata();
      if(!matches(wdata)) return null;
      if(xpathExtractor != null) {
        ExtractEntity entity = xpathExtractor.extractEntity(context);
        if(entity != null) return entity;
      }

      if(autoExtractor != null) {
        ExtractEntity entity = autoExtractor.extractEntity(context);
        if(entity != null) return entity;
      }
      return null;
    }

    boolean matches(WData wdata) {
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
  
  static public class AutoExtractor  {
    private WDataExtractor extractor;
    
    public AutoExtractor(ExtractConfig extractConfig, WDataExtractor extractor) {
      this.extractor     = extractor;
    }
    
    public ExtractEntity extractEntity(WDataExtractContext context) {
      ExtractEntity entity =  extractor.extractEntity(context);
      if(entity != null) entity.addTag("extractor:auto");
      return entity;
    }
  }
  
  static public class XPathExtractor  {
    ExtractConfig extractConfig;
    
    public XPathExtractor(ExtractConfig extractConfig) { 
      this.extractConfig = extractConfig;
    }
    
    public ExtractEntity extractEntity(WDataExtractContext context) {
      ExtractEntity extractEntity = new ExtractEntity(extractConfig.getName(), extractConfig.getExtractType().toString());
      XPathPattern[] xpathPattern = extractConfig.getExtractXPath();
      Document doc = context.getWdata().createJsoupDocument();
      doc.traverse(new NodeCleanerVisitor(NodeCleaner.IGNORE_NODE_CLEANER));
      int xpathExtractCount = 0;
      for(int i = 0; i < xpathPattern.length; i++) {
        String[] text = select(doc, xpathPattern[i].getXpath());
        if(text != null) {
          extractEntity.field(xpathPattern[i].getName(), text);
          xpathExtractCount++ ;
        }
      }
      if(xpathExtractCount > 0) {
        extractEntity.addTag("extractor:xpath");
        return extractEntity;
      }
      return null;
    }
    
    String[] select(Document doc, String xpath) {
      Elements elements = doc.select(xpath);
      if(elements.size() == 0) return null;
      String[] text = new String[elements.size()];
      FormatTextExtractor extractor = new FormatTextExtractor();
      for(int i = 0; i < text.length; i++) {
        Element ele = elements.get(i);
        text[i] = extractor.extract(ele);
      }
      return text;
    }
  }
}
