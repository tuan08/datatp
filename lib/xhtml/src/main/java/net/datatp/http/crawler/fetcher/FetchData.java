package net.datatp.http.crawler.fetcher;

import java.io.Serializable;

import net.datatp.http.ResponseHeaders;
import net.datatp.http.crawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class FetchData implements Serializable {
  private static final long serialVersionUID = 1L;

  private URLDatum          datum;
  private ResponseHeaders   responseHeaders  = new ResponseHeaders();
  private String            contentType;
  private byte[]            data;
  
  public FetchData() {} 

  public FetchData(URLDatum datum) {
    this.datum = datum ;
  }

  public URLDatum getURLDatum() { return datum; }
  public void setURLDatum(URLDatum datum) { this.datum = datum; }

  public ResponseHeaders getResponseHeaders() { return responseHeaders; }
  public void setResponseHeaders(ResponseHeaders responseHeaders) { 
    this.responseHeaders = responseHeaders; 
  }

  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }

  public byte[] getData() { return data; }
  public void setData(byte[] data) { this.data = data; }

}