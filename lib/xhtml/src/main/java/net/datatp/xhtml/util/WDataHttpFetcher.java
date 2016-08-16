package net.datatp.xhtml.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;

import net.datatp.util.URLParser;
import net.datatp.util.io.IOUtil;
import net.datatp.xhtml.WData;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 25, 2010  
 */
public class WDataHttpFetcher {
  private CloseableHttpClient  httpclient ;

  public WDataHttpFetcher() {
    httpclient =  HttpClients.createDefault();
  }

  public WData fetch(String urlString) throws Exception {
    return fetch("No Anchor Text", urlString) ;
  }

  public WData fetch(String anchorText, String urlString) throws Exception {  
    HttpGet httpget = new HttpGet(urlString);
    BasicHttpContext httpContext = new BasicHttpContext();
    httpContext.setAttribute("crawler.site", new URLParser(urlString).getHost()) ;
    HttpResponse response = httpclient.execute(httpget, httpContext);
    String content = IOUtil.getStreamContentAsString(response.getEntity().getContent(), "UTF-8");
    WData wPageData = new WData(urlString, anchorText, content);
    return wPageData ;
  }
}