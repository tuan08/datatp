package net.datatp.crawler.site;

import java.util.regex.Pattern;

import net.datatp.util.URLParser;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class SiteContext {
  
  private SiteConfig          siteConfig;
  private URLPatternMatcher[] urlPatternMatcher;
  private SiteScheduleStat    siteScheduleStat = new SiteScheduleStat();
  private URLStatistics       urlStatistics    = new URLStatistics();
  private SiteExtractor       siteExtractor;

  public SiteContext(SiteConfig config, SiteExtractor siteExtractor) {
    this.siteConfig    = config ;
    this.siteExtractor = siteExtractor;
    
    URLPattern[] urlPattern = config.getUrlPatterns();
    if(urlPattern != null) {
      urlPatternMatcher = new URLPatternMatcher[urlPattern.length];
      for(int i = 0; i < urlPattern.length; i++) {
        urlPatternMatcher[i] = new URLPatternMatcher(urlPattern[i]);
      }
    }
  }

  public SiteConfig getSiteConfig() { return this.siteConfig ; }
  
  public SiteScheduleStat getSiteScheduleStat() { return siteScheduleStat; }
  
  public URLStatistics getURLStatistics() { return urlStatistics ; }
  
  public SiteExtractor getSiteExtractor() { return siteExtractor; }
  
  public int getMaxConnection() { 
    int max = siteConfig.getMaxConnection() ;
    if(max < 1) max = 1 ;
    return max ;
  }
  
  public int getMaxSchedule() {
    return siteScheduleStat.getMaxSchedule(siteConfig.getMaxFetchSchedule(), getMaxConnection());
  }
  
  public boolean canSchedule() {
    return siteScheduleStat.canSchedule(siteConfig.getMaxFetchSchedule(), getMaxConnection());
  }

  public boolean allowDomain(URLParser urlParser) {
    String hostname = urlParser.getNormalizeHostName() ;
    if(hostname.equals(siteConfig.getHostname())) return true ;
    if(siteConfig.getCrawlSubDomain()) {
      return hostname.endsWith(siteConfig.getHostname()) ;
    } 
    return false ;
  }
  
  public URLPattern matchesIgnoreURLPattern(URLParser urlParser) {
    if(urlPatternMatcher != null) {
      String url = urlParser.getNormalizeURLAll();
      for(int i = 0; i < urlPatternMatcher.length; i++) {
        if(urlPatternMatcher[i].urlPattern.getType() != URLPattern.Type.ignore) continue;
        if(urlPatternMatcher[i].matches(url)) {
          return urlPatternMatcher[i].urlPattern;
        }
      }
    }
    return null;
  }
  
  public URLPattern matchesURLPattern(URLParser urlParser) {
    if(urlPatternMatcher != null) {
      String url = urlParser.getNormalizeURLAll();
      for(int i = 0; i < urlPatternMatcher.length; i++) {
        if(urlPatternMatcher[i].matches(url)) {
          return urlPatternMatcher[i].urlPattern;
        }
      }
    }
    return null;
  }
  
  
  public void update(SiteContext other) { this.siteConfig = other.siteConfig ; }

  static public class URLPatternMatcher {
    URLPattern urlPattern;
    Pattern[]  pattern;
    
    URLPatternMatcher(URLPattern urlPattern) {
      this.urlPattern = urlPattern;
      String[] patternExp = urlPattern.getPattern();
      pattern = new Pattern[patternExp.length];
      for(int i = 0; i < pattern.length; i++) {
        pattern[i] = Pattern.compile(patternExp[i]);
      }
    }
    
    public boolean matches(String url) {
      for(int i = 0; i < pattern.length; i++) {
        if(pattern[i].matcher(url).matches()) return true;
      }
      return false;
    }
  }
}