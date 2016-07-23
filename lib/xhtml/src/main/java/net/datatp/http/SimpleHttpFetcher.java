package net.datatp.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;

import net.datatp.util.URLParser;
import net.datatp.util.io.IOUtil;
import net.datatp.xhtml.XhtmlDocument;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 25, 2010  
 */
public class SimpleHttpFetcher {
  private CloseableHttpClient  httpclient ;

  public SimpleHttpFetcher() {
    this.httpclient =  HttpClients.createDefault();
  }

  public XhtmlDocument fetch(String urlString) throws Exception {
    return fetch("No Anchor Text", urlString) ;
  }

  public XhtmlDocument fetch(String anchorText, String urlString) throws Exception {  
    HttpGet httpget = new HttpGet(urlString);
    BasicHttpContext httpContext = new BasicHttpContext();
    httpContext.setAttribute("crawler.site", new URLParser(urlString).getHost()) ;
    HttpResponse response = httpclient.execute(httpget, httpContext);
    String content = IOUtil.getStreamContentAsString(response.getEntity().getContent(), "UTF-8");
    XhtmlDocument xdoc = new XhtmlDocument(urlString, anchorText, content);
    return xdoc ;
  }
}