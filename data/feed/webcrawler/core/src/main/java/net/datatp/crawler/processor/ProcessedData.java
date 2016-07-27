package net.datatp.crawler.processor;

import java.util.List;

import net.datatp.crawler.urldb.URLDatum;
import net.datatp.xhtml.WData;

public class ProcessedData {
  private URLDatum       urlDatum;
  private WData  wPageData;
  private List<URLDatum> extractURls;
  
  public URLDatum getUrlDatum() { return urlDatum; }
  public void setUrlDatum(URLDatum urlDatum) { this.urlDatum = urlDatum; }
  
  public WData getWPageData() { return wPageData; }
  public void setWPageData(WData wPageData) { this.wPageData = wPageData;}
  
  public List<URLDatum> getExtractURls() { return extractURls; }
  public void setExtractURls(List<URLDatum> extractURls) { this.extractURls = extractURls; }
}
