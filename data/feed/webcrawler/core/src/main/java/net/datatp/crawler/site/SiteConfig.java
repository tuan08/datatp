package net.datatp.crawler.site;

import java.io.Serializable;

import net.datatp.util.text.StringUtil;

public class SiteConfig implements Serializable {
  static public enum Status {Good, Ok, New, Pending, Review, Deleted, Moved, Empty}

  private String   group  = "default";
  private String   hostname;
  private String[] injectUrl;
  private String[] ignoreUrlPattern;
  private String[] tags;
  private boolean  crawlSubDomain;
  private int      crawlDeep;
  private int      refreshPeriod;
  private int      maxConnection;
  private int      maxFetchSchedule = 100;
  private Status   status = Status.Ok;
  private String   language;
  private String   description;

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
  
  public String getGroup() { return group; }
  public void setGroup(String group) { this.group = group; }

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

  public int getMaxConnection() { return maxConnection ; }
  public void setMaxConnection(int value) { maxConnection = value ; }

  public int getMaxFetchSchedule() { return maxFetchSchedule; }
  public void setMaxFetchSchedule(int maxFetchSchedule) {
    this.maxFetchSchedule = maxFetchSchedule;
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
}