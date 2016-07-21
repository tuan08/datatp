package net.datatp.http.crawler.fetcher;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.http.ErrorCode;
import net.datatp.http.ResponseCode;
import net.datatp.http.ResponseHeaders;
import net.datatp.http.crawler.site.URLContext;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.util.io.IOUtil;
import net.datatp.xhtml.XhtmlDocument;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 23, 2010  
 */
public class SiteSession implements Comparable<SiteSession> {
  private static final Logger logger = LoggerFactory.getLogger(SiteSession.class);

  private String              hostname ;
  private CookieStore         cookieStore ;
  private ErrorCheckCondition errorCheckCondition ;
  private boolean             lock = false ;

  public SiteSession(String hostname) {
    this.hostname = hostname ;
    this.cookieStore = new BasicCookieStore();
  }

  public boolean isLocked() { return lock  ; }

  synchronized public FetchData fetch(CloseableHttpClient httpclient, URLDatum urldatum, URLContext context)  {
    FetchData fdata = new FetchData(urldatum);
    if(errorCheckCondition != null) {
      if(errorCheckCondition.isExpired()) {
        errorCheckCondition = null ;
      } else {
        errorCheckCondition.handle(urldatum, context) ;
        return fdata;
      }
    }
    try {
      lock = true ;
      long startTime = System.currentTimeMillis() ;
      String fetchUrl = urldatum.getFetchUrl();
      
      HttpGet httpget = new HttpGet(fetchUrl); 
      BasicHttpContext httpContext = new BasicHttpContext();
      httpContext.setAttribute("crawler.site", hostname) ;
      httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
      HttpResponse response = httpclient.execute(httpget, httpContext);
      
      String redirectUrl = (String)httpContext.getAttribute("url.redirect") ;
      if(redirectUrl != null) {
        urldatum.setRedirectUrl(redirectUrl) ;
      }
      String url = urldatum.getOriginalUrlAsString() ;
      XhtmlDocument xdoc = new XhtmlDocument(url, urldatum.getAnchorTextAsString(), null) ;

      fdata.setResponseHeaders(getResponseHeaders(response));
      fdata.setContentType(HttpClientUtil.getContentType(response)) ;
      StatusLine sline = response.getStatusLine() ;
      urldatum.setLastResponseCode((short)sline.getStatusCode()) ;
      urldatum.setContentType(xdoc.getContentType()) ;
      
      byte[] data = handleContent(context, urldatum, response);
      fdata.setData(data);
      long downloadTime = System.currentTimeMillis() - startTime ;
      urldatum.setLastFetchDownloadTime(downloadTime) ;
    } catch(Throwable t) {
      handleError(fdata.getURLDatum(), context, getRootCause(t)) ;
    } finally {
      lock = false ;
    }
    return fdata;
  }

  public int compareTo(SiteSession other) {
    return hostname.compareTo(other.hostname);
  }

  Throwable getRootCause(Throwable t) {
    Throwable parent = t.getCause() ;
    while(parent != null) {
      t = parent ;
      parent = t.getCause() ;
    }
    return t ;
  }

  ResponseHeaders getResponseHeaders(HttpResponse response) {
    ResponseHeaders responseHeaders = new ResponseHeaders();
    Header[] headers = response.getAllHeaders() ;
    for(int i = 0; i < headers.length; i++) {
      responseHeaders.setHeader(headers[i].getName(), headers[i].getValue()) ;
    }
    responseHeaders.setResponseCode(response.getStatusLine().getStatusCode()) ;
    return responseHeaders;
  }

  byte[] handleContent(URLContext context, URLDatum datum, HttpResponse response) throws Exception {
    InputStream is = response.getEntity().getContent() ;
    byte[] data = IOUtil.getStreamContentAsBytes(is, 300000) ;
    if(data != null) datum.setLastDownloadDataSize(data.length) ;
    else datum.setLastDownloadDataSize(0) ;
    return data;
  }
  
  void handleError(URLDatum urlDatum, URLContext context, Throwable error) {
    if(error instanceof URISyntaxException) {
      urlDatum.setLastResponseCode(ResponseCode.ILLEGAL_URI) ;
    } else if(error instanceof SSLPeerUnverifiedException) {
      urlDatum.setLastErrorCode(ErrorCode.ERROR_CONNECTION_NOT_AUTHORIZED) ;
      urlDatum.setLastResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    } else if(error instanceof SocketTimeoutException) {
      urlDatum.setLastErrorCode(ErrorCode.ERROR_CONNECTION_SOCKET_TIMEOUT) ;
      urlDatum.setLastResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    } else if(error instanceof UnknownHostException) {
      errorCheckCondition = new ConnectionCheckCondition(ErrorCode.ERROR_CONNECTION_UNKNOWN_HOST, 5 * 60 *1000) ;
      errorCheckCondition.handle(urlDatum, context) ;
    } else if(error instanceof ConnectTimeoutException) {
      //Cannot etablish the connection to the server
      errorCheckCondition = new ConnectionCheckCondition(ErrorCode.ERROR_CONNECTION_TIMEOUT, 5 * 60 *1000) ;
      errorCheckCondition.handle(urlDatum, context) ;
    } else if(error instanceof ConnectException) {
      ConnectException cex = (ConnectException) error ;
      if(cex.getMessage().indexOf("timed out") >= 0) {
        urlDatum.setLastErrorCode(ErrorCode.ERROR_CONNECTION_TIMEOUT) ;
      } else {
        urlDatum.setLastErrorCode(ErrorCode.ERROR_CONNECTION) ;
      }
      urlDatum.setLastResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    } else {
      logger.error("Error For URL: " + urlDatum.getOriginalUrlAsString(), error) ;
      urlDatum.setLastResponseCode(ResponseCode.UNKNOWN_ERROR) ; 
    }
  }

  static interface ErrorCheckCondition {
    public boolean isExpired() ;
    public void    handle(URLDatum urlDatum, URLContext context) ;
  }

  static class ConnectionCheckCondition implements ErrorCheckCondition {
    private byte errorCode ;
    private long expiredAt ;

    ConnectionCheckCondition(byte errorCode, long checkPeriod) {
      this.errorCode = errorCode ;
      this.expiredAt = System.currentTimeMillis() + checkPeriod ;
    }

    public boolean isExpired() { return System.currentTimeMillis() > expiredAt ; }

    public void handle(URLDatum urlDatum, URLContext context) {
      urlDatum.setLastErrorCode(this.errorCode) ;
      urlDatum.setLastResponseCode(ResponseCode.UNKNOWN_ERROR) ;
    }
  }
}