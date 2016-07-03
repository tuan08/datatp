package net.datatp.webcrawler.site;

import java.io.Serializable;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.extract.XpathConfig;

public class SiteConfig implements Serializable {
  final public static String STATUS_GOOD     = "good" ;
  final public static String STATUS_OK       = "ok" ;

  final public static String STATUS_NEW      = "new" ;
  final public static String STATUS_PENDING  = "pending" ;
  final public static String STATUS_REVIEW   = "review" ;
  final public static String STATUS_DELETED  = "deleted" ;
  final public static String STATUS_MOVED    = "moved" ;
  final public static String STATUS_EMPTY    = "empty" ;
  final public static String[] STATUS_ALL    = {
      STATUS_GOOD, STATUS_OK, STATUS_NEW, STATUS_PENDING, 
      STATUS_REVIEW, STATUS_DELETED, STATUS_MOVED, STATUS_EMPTY 
  };

  final public static String IMPORT_PAGE     = "import-page" ;
  final public static String TAG             = "tag" ;
  final public static String CRAWL_SUB_DOMAIN= "crawl-sub-domain" ;
  final public static String CRAWLER_DEEP    = "crawl-deep" ;
  final public static String REFRESH_PERIOD  = "refresh-period" ;
  final public static String MAX_CONNECTION  = "max-connection" ;
  final public static String STATUS          = "status" ;
  final public static String LANGUAGE        = "language" ;

  final static public String FIELD_SEPARATOR = "|" ;

  private String               hostname;
  private String[]             injectUrl;
  private String[]             ignoreUrlPattern;
  private String[]             tags;
  private boolean              crawlSubDomain;
  private int                  crawlDeep;
  private int                  refreshPeriod;
  private int                  maxConnection;
  private XpathConfig[]        xpathConfig;
  private String               status           = STATUS_OK;
  private String               language;
  private String               description;


  public SiteConfig() {}

  public SiteConfig(String hostname) {
    this.hostname = hostname ;
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

  public int getMaxConnection() { return maxConnection ; }
  public void setMaxConnection(int value) { maxConnection = value ; }

  public XpathConfig[] getXpathConfig() { return this.xpathConfig ; }
  public void setXpathConfig(XpathConfig[] config) { this.xpathConfig = config ; }

  public String getStatus() { 
    if(status == null) return STATUS_REVIEW ;
    return status ; 
  }

  public void   setStatus(String status) { this.status = status ; }

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