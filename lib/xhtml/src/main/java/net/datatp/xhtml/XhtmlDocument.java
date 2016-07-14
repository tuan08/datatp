package net.datatp.xhtml;

import java.io.Serializable;

import org.jsoup.nodes.Document;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.parser.JSoupParser;

public class XhtmlDocument implements Serializable {
  private static final long serialVersionUID = 1L;

  private String              url;
  private String              anchorText;
  private String              xhtml;
  private String              contentType;
  private ResponseHeaders     headers = new ResponseHeaders();
  private String[]            tag ;
  private Document            jsoupDocument;
  
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

  public ResponseHeaders getHeaders() { return headers; }
  
  public String getContentType() { return contentType; }
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void addTag(String tag) {
    this.tag = StringUtil.merge(this.tag, tag) ;
  }
  
  public void addTag(String[] tag) {
    this.tag = StringUtil.merge(this.tag, tag) ;
  }
  
  public void addTag(String prefix, String[] tag) {
    if(tag == null) return ;
    String[] newTag = new String[tag.length] ;
    for(int i = 0; i < tag.length; i++) newTag[i] = prefix +  tag[i] ;
    this.tag = StringUtil.merge(this.tag, newTag) ;
  }
  
  public boolean hasTag(String tag) { return StringUtil.isIn(tag, this.tag) ; }
  
  public String[] getTags() { return tag ; }
  public void     setTags(String[] tag) { this.tag = tag ; }
  
  public Document getJsoupDocument() { 
    if(jsoupDocument == null) jsoupDocument = JSoupParser.INSTANCE.parse(xhtml);
    return jsoupDocument;
  }
}