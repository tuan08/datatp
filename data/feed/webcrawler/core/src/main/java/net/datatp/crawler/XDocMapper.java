package net.datatp.crawler;

import java.io.Serializable;
import java.util.Date;

import net.datatp.xhtml.XDoc;

public class XDocMapper {
  final static public String URL   = "url";
  final static public String MD5ID = "md5Id";
  
  private XDoc xdoc;
  
  public XDocMapper(XDoc xdoc) {
    this.xdoc = xdoc;
  }
  
  public XDocMapper() {
    xdoc = new XDoc();
    xdoc.setTimestamp(new Date());
  }
  
  public XDoc getXDoc() { return xdoc; }
  
  public String getMD5Id() { return xdoc.attr(MD5ID); }
  public void   setMD5Id(String md5Id) { xdoc.attr(MD5ID, md5Id); }
  
  public String getUrl() { return xdoc.attr(URL) ;}
  public void setUrl(String url) { xdoc.attr(URL, url); }
  
  public String getErrorResponseCode() { return xdoc.attr("errorResponseCode") ; }
  public void   setErrorResponseCode(String code) { xdoc.attr("errorCesponseCode", code) ; }
  
  public String getErrorContent() { return xdoc.attr("error.content") ; }
  public void   setErrorContent(String content) { xdoc.attr("errorContent", content) ; }
  
  public void addEntity(String name, Serializable obj) {
    xdoc.addEntity(name, obj);
  }
}
