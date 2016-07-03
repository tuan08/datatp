package net.datatp.xhtml;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.datatp.util.text.StringUtil;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 27, 2010  
 */
public class XhtmlLink implements Serializable {
  private String   url ;
  private String   anchorText ;
  private int      deep ;
  private String[] tag ;

  public XhtmlLink() {
  }

  public XhtmlLink(String label, String url) {
    this.anchorText = label ;
    this.url = url ;
  }

  public String getURL() { return this.url ; }
  public void   setURL(String url) { this.url = url ; }

  public String getAnchorText() { return this.anchorText ; }
  public void   setAnchorText(String s) { this.anchorText = s ; }

  public int  getDeep()  { return this.deep ; }
  public void setDeep(int deep) { this.deep = deep ; }

  public void addTag(String tag) {
    this.tag = StringUtil.merge(this.tag, tag) ;
  }
  public boolean hasTag(String tag) {
    return StringUtil.isIn(tag, this.tag) ;
  }
  public String[] getTags() { return tag ; }
  public void setTags(String[] tag) {
    this.tag = tag ;
  }

  @JsonIgnore
  public String getDomain() {
    String string = url ;
    int idx = url.indexOf("://") ;
    if(idx > 0) {
      string = string.substring(idx + 3) ;
    }
    idx = string.indexOf("/") ;
    if(idx > 0) {
      string = string.substring(0, idx) ;
    }
    return string ;
  }
}