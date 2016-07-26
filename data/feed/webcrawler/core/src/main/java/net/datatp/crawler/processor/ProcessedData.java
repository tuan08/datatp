package net.datatp.crawler.processor;

import java.util.List;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.XhtmlDocument;

public class ProcessedData {
  private URLDatum       urlDatum;
  private XhtmlDocument  xhtmlDocument;
  private List<URLDatum> extractURls;
  
  public URLDatum getUrlDatum() { return urlDatum; }
  public void setUrlDatum(URLDatum urlDatum) { this.urlDatum = urlDatum; }
  
  public XhtmlDocument getXhtmlDocument() { return xhtmlDocument; }
  public void setXhtmlDocument(XhtmlDocument xhtmlDocument) { this.xhtmlDocument = xhtmlDocument;}
  
  public List<URLDatum> getExtractURls() { return extractURls; }
  public void setExtractURls(List<URLDatum> extractURls) { this.extractURls = extractURls; }
}
