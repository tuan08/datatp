package net.datatp.crawler;

import java.util.Date;
import java.util.Map;

import net.datatp.xhtml.XDoc;
import net.datatp.xhtml.XDocEntity;

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
  public void   setErrorResponseCode(String code) { xdoc.attr("errorResponseCode", code) ; }
  
  public String getErrorContent() { return xdoc.attr("errorContent") ; }
  public void   setErrorContent(String content) { xdoc.attr("errorContent", content) ; }
  
  public void setPageType(String type) { xdoc.attr("pageType", type); }
  
  public boolean hasEntities() {
    Map<String, XDocEntity> entities = xdoc.getEntities();
    if(entities != null && entities.size() > 0) return true;
    return false;
  }
  
  public void addEntity(XDocEntity entity) { 
    xdoc.addEntity(entity); 
  }
}
