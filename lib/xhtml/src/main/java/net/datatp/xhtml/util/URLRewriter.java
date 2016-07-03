package net.datatp.xhtml.util;

import net.datatp.util.text.CharacterSet;

public class URLRewriter {
  public String rewrite(String siteURL, String baseURL, String urlToRewrite) {
    if(siteURL == null || baseURL == null || urlToRewrite == null) { 
      throw new RuntimeException("site url, base url or url is null") ;
    }
    try {
      StringBuilder b = new StringBuilder() ;
      char[] buf = urlToRewrite.toCharArray() ;
      for(int i = 0; i < buf.length; i++) {
        if(CharacterSet.isBlank(buf[i])) continue ;
        b.append(buf[i]) ;
      }
      urlToRewrite = b.toString() ;	


      if(urlToRewrite.indexOf("://") > 0) {
        return urlToRewrite ;
      } 

      int idx = urlToRewrite.indexOf(':') ;
      if(idx > 0 ) {
        String prefix = urlToRewrite.substring(0, idx).toLowerCase() ;
        if("javascript".equals(prefix)) return urlToRewrite ;
        if("mailto".equals(prefix)) return urlToRewrite ;
        if("ymsgr".equals(prefix)) return urlToRewrite ;
        if("skype".equals(prefix)) return urlToRewrite ;
        if("msnim".equals(prefix)) return urlToRewrite ;
        if("aim".equals(prefix)) return urlToRewrite ;
        if("jabber".equals(prefix)) return urlToRewrite ;
      }

      if(urlToRewrite.startsWith("/")) {
        return siteURL + urlToRewrite ;
      } else if(urlToRewrite.startsWith("../")) {
        baseURL = baseURL.substring(0, baseURL.lastIndexOf("/")) ;
        urlToRewrite = urlToRewrite.substring(3, urlToRewrite.length()) ;
        return rewrite(siteURL, baseURL, urlToRewrite) ;
      } else {
        if(baseURL.endsWith("/")) return baseURL  + urlToRewrite ;
        return baseURL + "/" + urlToRewrite ;
      }
    } catch(Throwable t) {}
    return urlToRewrite ;
  }
}

