package net.datatp.xhtml.fetcher;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import net.datatp.util.io.IOUtil;
import net.datatp.xhtml.XhtmlDocument;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 25, 2010  
 */
public class URLConnectionFetcher implements Fetcher {
  public XhtmlDocument fetch(String urlString) throws Exception {
    urlString = URLEncoder.encode(urlString);
    URL url = new URI(urlString).normalize().toURL();
    URLConnection uc = url.openConnection();
    uc.setReadTimeout(200000);
    String content = IOUtil.getStreamContentAsString(uc.getInputStream(), "UTF-8");
    XhtmlDocument xdoc = new XhtmlDocument(urlString, null, content);
    return xdoc ;
  }
}