package net.datatp.http.crawler;

import java.io.Serializable;

import net.datatp.util.text.StringUtil;

public class SiteConfig implements Serializable {
  private String               hostname;
  private String[]             injectUrl;
  private String[]             ignoreUrlPattern;
  private String[]             tags;
  private boolean              crawlSubDomain;
  private int                  crawlDeep;
  private int                  refreshPeriod;
  private String               language;
  private String               description;

  public SiteConfig() {}

  public SiteConfig(String hostname) {
    this.hostname = hostname ;
  }

  public SiteConfig(String site, String injectUrl, int crawlDeep) {
    setHostname(site) ;
    setInjectUrl(new String[] {injectUrl}) ;
    setCrawlDeep(crawlDeep) ;
    setRefreshPeriod(60 * 60 * 24) ;
  }
  
  public String getHostname() { return hostname ; }
  public void   setHostname(String value) { hostname = value ; }

  public String[] getInjectUrl() { return injectUrl ; }
  public void     setInjectUrl(String[] value) { injectUrl = value ; }

  public String[] getIgnoreUrlPattern() { return ignoreUrlPattern ; }
  public void     setIgnoreUrlPattern(String[] value) { ignoreUrlPattern = value ; }

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

  public String getLanguage() { return this.language ; }
  public void setLanguage(String language) { this.language = language ; }

  public String getDescription() { return this.description ; }
  public void setDescription(String desc) { this.description = desc ; }

  public String reverseHostName() {
    String result = null ;
    String hostName = getHostname() ;
    String[] array = StringUtil.toStringArray(hostName, "\\.") ;
    for(int i = array.length - 1; i >= 0; i--) {
      if(result == null) result = array[i] ; 
      else result += "." + array[i] ;
    }
    return result ;
  }
}