package net.datatp.crawler.fetcher;

import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.util.io.IOUtil;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 10, 2010  
 */
public class HttpClientUtil {
  static public void printRequestHeaders(HttpRequest req) {
    Header[] header = req.getAllHeaders() ;
    for(int i = 0; i < header.length; i++) {
      if(i > 0) System.out.print(" # ") ;
      System.out.print(header[i].getName() + ": " + header[i].getValue()) ;
    }
    System.out.println();
  }
  
  static public void printResponseHeaders(HttpResponse res) {
    Header[] header = res.getAllHeaders() ;
    for(int i = 0; i < header.length; i++) {
      System.out.println(header[i].getName() + ": " + header[i].getValue()) ;
    }
    System.out.println();
  }
  
  static public String getResponseHeaders(HttpResponse res) {
    StringBuilder b = new StringBuilder() ;
    Header[] header = res.getAllHeaders() ;
    for(int i = 0; i < header.length; i++) {
      b.append(header[i].getName() + ": " + header[i].getValue()).append("\n") ;
    }
    b.append("Status Code: "+res.getStatusLine().getStatusCode());
    return b.toString() ;
  }
  
  static public String getResponseBody(HttpResponse res) throws Exception {
    InputStream is = res.getEntity().getContent() ;
    byte[] data = IOUtil.getStreamContentAsBytes(is) ;
    return new String(data, "UTF-8");
  }
  
  static public void printResponseBody(HttpResponse res) throws Exception {
    InputStream is = res.getEntity().getContent() ;
    byte[] data = IOUtil.getStreamContentAsBytes(is) ;
    System.out.println(new String(data, "UTF-8"));
  }
  
  static public void printCookiStore(CookieStore store) throws Exception {
    List<Cookie> cookies = store.getCookies() ;
    for(int i = 0; i < cookies.size(); i++) {
      Cookie cookie = cookies.get(i) ;
      System.out.println("Cookie " + cookie.getDomain() + ": "+ cookie.getName() + " = " + cookie.getValue()) ;
    }
  }
  
  static public String getContentType(HttpResponse res) {
    Header header = res.getFirstHeader("Content-WebPageType") ;
    if(header == null) return URLDatum.DEFAULT_CONTENT_TYPE.toString();
    String value =  header.getValue();
    int idx = value.indexOf(";") ;
    if(idx > 0) {
      value = value.substring(0, idx).trim() ;
    }
    return value ;
  }
}
