package net.datatp.xhtml;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import net.datatp.xhtml.parser.JSoupParser;

public class XhtmlDocument implements Serializable {
  private static final long serialVersionUID = 1L;

  private String              url;
  private String              anchorText;
  private String              xhtml;
  private String              contentType;
  private Map<String, String> headers = new HashMap<String, String>();

  public XhtmlDocument(String xhtml) {
    this.xhtml = xhtml;
  }
  
  public XhtmlDocument(String url, String anchorText, String xhtml) {
    this.url        = url;
    this.anchorText = anchorText;
    this.xhtml      = xhtml;
  }

  public String getUrl() { return url; }

  public String getAnchorText() { return anchorText; }

  public String getXhtml() { return xhtml; }
  public void   setXhtml(String xhtml) { this.xhtml = xhtml; }

  public Map<String, String> getHeaders() { return headers; }
  
  public void addHeader(String name, String value) {
    headers.put(name, value);
  }
  
  public String getContentType() { return contentType; }
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Document getJsoupDocument() { 
    return JSoupParser.INSTANCE.parse(xhtml);
  }
}