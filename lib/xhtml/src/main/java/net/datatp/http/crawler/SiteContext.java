package net.datatp.http.crawler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.datatp.util.URLParser;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class SiteContext {
  private SiteConfig siteConfig ;
  private Map<String, Serializable> attributes = new HashMap<String, Serializable>() ;

  public SiteContext(SiteConfig config) {
    this.siteConfig = config ;
  }

  public SiteConfig getSiteConfig() { return this.siteConfig ; }
  public void setSiteConfig(SiteConfig config) { this.siteConfig = config ; }

  public <T extends Serializable> boolean hasAttribute(Class<T> clazz) {
    return attributes.containsKey(clazz.getName())  ;
  }

  public <T extends Serializable> T getAttribute(Class<T> clazz) {
    return getAttribute(clazz, true) ;
  }

  public <T extends Serializable> T getAttribute(Class<T> clazz, boolean create) {
    T instance = (T) attributes.get(clazz.getName()) ;
    if(instance == null && create) {
      try {
        instance = clazz.newInstance() ;
        attributes.put(clazz.getName(), instance) ;
      } catch (InstantiationException e) {
        throw new RuntimeException(e) ;
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e) ;
      }
    }
    return instance ;
  }

  public boolean allowURL(URLParser urlnorm) {
    String hostname = urlnorm.getNormalizeHostName() ;
    if(hostname.equals(siteConfig.getHostname())) return true ;
    if(siteConfig.getCrawlSubDomain()) {
      return hostname.endsWith(siteConfig.getHostname()) ;
    } 
    return false ;
  }

  public void update(SiteContext other) { this.siteConfig = other.siteConfig ; }
}