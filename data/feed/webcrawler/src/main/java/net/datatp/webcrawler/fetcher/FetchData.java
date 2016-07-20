package net.datatp.webcrawler.fetcher;

import java.io.Serializable;

import net.datatp.webcrawler.urldb.URLDatum;
import net.datatp.xhtml.XhtmlDocument;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 14, 2010  
 */
public class FetchData implements Serializable {
  private static final long serialVersionUID = 1L;

  private URLDatum datum ;
  private XhtmlDocument document ;

  public FetchData() {} 

  public FetchData(URLDatum datum) {
    this.datum = datum ;
  }


  public URLDatum getURLDatum() { return this.datum ; }

  public XhtmlDocument getDocument() { return this.document ; }
  public void setDocument(XhtmlDocument doc) { this.document = doc ; } 

  public void setResponseCode(short code) { this.datum.setLastResponseCode(code) ; }

  public void setDownloadTime(long time) { this.datum.setLastFetchDownloadTime(time) ; }

}