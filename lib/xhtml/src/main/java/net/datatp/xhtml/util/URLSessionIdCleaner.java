package net.datatp.xhtml.util;

import java.util.Map;

import net.datatp.util.URLParser;
import net.datatp.util.URLNormalizerProcessor;


/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Aug 2, 2010  
 */
public class URLSessionIdCleaner implements URLNormalizerProcessor {
  public void process(URLParser urlnorm) {
    Map<String, String[]> params = urlnorm.getParams() ;
    if(params != null) {
      processSParam(params) ;
    }
  }

  private void processSParam(Map<String, String[]> params) {
    String[] uuidName = {"s","ses", "sid", "SID", "zenid", "NVS"} ;
    for(String sel : uuidName) {
    	String[] val = params.get(sel) ;
    	if(val == null) continue ;
    	String fval = val[0] ;
    	if(fval.length() == 32 && isHexString(fval)) {
    		params.remove(sel) ;
    		break ;
    	} else if(fval.startsWith("qh") || fval.startsWith("kh")) {
    		params.remove(sel) ;
    		break ;
    	}
    }
    params.remove("osCsid") ;
    params.remove("PHPSESSID") ;
  }
  
  private boolean isHexString(String string) {
    for(int i = 0; i < string.length(); i++) {
      char c = string.charAt(i) ;
      if(c >= '0' && c <= '9') continue ;
      if(c >= 'a' && c <= 'f') continue ;
      if(c >= 'A' && c <= 'F') continue ;
      return false ;
    }
    return true ;
  }
}
