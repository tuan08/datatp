package net.datatp.crawler.fetcher;

import java.util.ArrayList;

import net.datatp.crawler.XDocMapper;
import net.datatp.crawler.http.ResponseHeaders;
import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class FetchContext {
  private HttpFetcher         httpFetcher;
  private SiteSession         session;
  private URLContext          urlContext;
  private ResponseHeaders     responseHeaders = new ResponseHeaders();
  private String              contentType;
  private byte[]              data;
  private ArrayList<URLDatum> commitURLs;
  private XDocMapper          xdoc = new XDocMapper();
  
  public FetchContext(HttpFetcher fetcher, SiteSession session, URLContext urlContext) {
    this.httpFetcher = fetcher;
    this.session     = session;
    this.urlContext  = urlContext;
  }

  public XDocMapper getXDocMapper() { return xdoc; }
  
  public HttpFetcher getHttpFetcher() { return this.httpFetcher ; }
  public SiteSession getSiteSession() { return this.session; }
  
  public ResponseHeaders getResponseHeaders() { return responseHeaders; }
  public void setResponseHeaders(ResponseHeaders responseHeaders) { 
    this.responseHeaders = responseHeaders; 
  }

  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }

  public byte[] getData() { return data; }
  public void setData(byte[] data) { this.data = data; }

  public URLContext getURLContext() { return urlContext; }

  public ArrayList<URLDatum> getCommitURLs() { return commitURLs; }
  public void setCommitURLs(ArrayList<URLDatum> extractURLs) { this.commitURLs = extractURLs; }
}