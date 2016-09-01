package net.datatp.crawler.site;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.datatp.util.text.StringUtil;

public class SiteConfig implements Serializable {
  private static final long serialVersionUID = 1L;

  static public enum Status {Good, Ok, New, Pending, Review, Deleted, Moved, Empty}

  private String               group            = "default";
  private String               hostname;
  private String[]             injectUrl;
  private String[]             tags;

  private boolean              crawlSubDomain   = false;
  private int                  crawlDeep;
  private int                  refreshPeriod;
  private int                  maxConnection    = 1;
  private int                  maxFetchSchedule = 100;
  private WebPageTypePattern[] webPageTypePatterns;

  private Status               status           = Status.Ok;
  private String               language;
  private String               description;

  private ExtractConfig[]      extractConfig;
  
  public SiteConfig() {}

  public SiteConfig(String hostname) {
    this.hostname = hostname ;
  }

  public SiteConfig(String group, String site, String injectUrl, int crawlDeep) {
    setGroup(group);
    setHostname(site) ;
    setInjectUrl(new String[] {injectUrl}) ;
    setCrawlDeep(crawlDeep) ;
    setStatus(Status.Ok) ;
    setRefreshPeriod(60 * 60 * 24) ;
  }

  public String relativeStorePath() { return group + "/" + hostname; }
  
  public String getGroup() { return group; }
  public void setGroup(String group) { this.group = group; }

  public String getHostname() { return hostname ; }
  public void   setHostname(String value) { hostname = value ; }

  public String[] getInjectUrl() { return injectUrl ; }
  public void     setInjectUrl(String[] value) { injectUrl = value ; }

  public boolean hasTag(String tag) {
    if(tags == null) return false ;
    return StringUtil.isIn(tag, tags) ;
  }

  public String[] getTags() { return tags ; }
  public void setTags(String[] value) { tags = value ; }

  public void addTag(String value) { 
    this.tags = StringUtil.merge(tags, value) ;
  }

  public boolean getCrawlSubDomain() { return this.crawlSubDomain ; }
  public void setCrawlSubDomain(boolean b) { this.crawlSubDomain =  b ; }

  public int getCrawlDeep() { return crawlDeep ; }
  public void setCrawlDeep(int value) { crawlDeep = value ; }

  public int getRefreshPeriod() { return refreshPeriod ; }
  public void setRefreshPeriod(int value) { refreshPeriod = value ; }

  public int getMaxConnection() { return maxConnection ; }
  public void setMaxConnection(int value) { maxConnection = value ; }

  public int getMaxFetchSchedule() { return maxFetchSchedule; }
  public void setMaxFetchSchedule(int maxFetchSchedule) {
    this.maxFetchSchedule = maxFetchSchedule;
  }

  public WebPageTypePattern[] getWebPageTypePatterns() { return webPageTypePatterns; }
  public void setWebPageTypePatterns(WebPageTypePattern ... urlPatterns) { 
    if(urlPatterns != null) {
      List<WebPageTypePattern> holder = new ArrayList<>();
      for(int i = 0; i < urlPatterns.length; i++) {
        String[] pattern = urlPatterns[i].getPattern();
        if(pattern != null && pattern.length > 0) {
          holder.add(urlPatterns[i]);
        }
      }
      urlPatterns = holder.toArray(new WebPageTypePattern[holder.size()]);
      Arrays.sort(urlPatterns, WebPageTypePattern.PRIORITY_COMPARATOR);
    } 
    this.webPageTypePatterns = urlPatterns;
  }

  public Status getStatus() { 
    if(status == null) return Status.Review ;
    return status ; 
  }

  public void   setStatus(Status status) { this.status = status ; }

  public String getLanguage() { return this.language ; }
  public void setLanguage(String language) { this.language = language ; }

  public String getDescription() { return this.description ; }
  public void setDescription(String desc) { this.description = desc ; }

  public ExtractConfig[] getExtractConfig() { return extractConfig; }

  public SiteConfig setExtractConfig(ExtractConfig ... extractConfig) {
    this.extractConfig = extractConfig; 
    return this;
  }
}