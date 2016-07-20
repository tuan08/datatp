package net.datatp.http;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class CrawlerRedirectHandler extends DefaultRedirectStrategy {

  @Override
  public boolean isRedirected(HttpRequest request, HttpResponse res, HttpContext context) throws ProtocolException {
    Header header = res.getFirstHeader("Location") ;
    //HttpClientUtil.printResponseHeaders(res) ;O
    if(header != null) {
      String location = header.getValue() ;
      if(location.indexOf("://") > 0) {
        String site = (String)context.getAttribute("crawler.site") ;
        int idx = location.indexOf("://") ;
        String hostName = location.substring(idx + 3) ;
        idx = hostName.indexOf('/') ;
        if(idx > 0) {
          hostName = hostName.substring(0, idx) ;
        }
        context.setAttribute("url.redirect", location) ;
        if(!hostName.endsWith(site)) return false ;
      }
    }
    return super.isRedirected(request, res, context);
  }

}
