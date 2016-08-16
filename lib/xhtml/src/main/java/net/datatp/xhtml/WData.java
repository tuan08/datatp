package net.datatp.xhtml;

import java.io.Serializable;
import java.util.Map;

import org.jsoup.nodes.Document;

import net.datatp.util.text.StringUtil;

public class WData implements Serializable {
  private static final long serialVersionUID = 1L;

  private String              url;
  private String              anchorText;
  private byte[]              data;
  private String              contentType;
  private String[]            tag ;
  private Map<String, Object> attrs;

  public WData(String xhtml) {
    data = xhtml.getBytes(StringUtil.UTF8);
  }

  public WData(String url, String anchorText, String xhtml) {
    this(url, anchorText, xhtml.getBytes(StringUtil.UTF8));
  }

  public WData(String url, String anchorText, byte[] data) {
    this.url         = url;
    this.anchorText  = anchorText;
    this.data        = data;
  }
  
  public String getUrl() { return url; }

  public String getAnchorText() { return anchorText; }

  public String getDataAsXhtml() { return new String(data, StringUtil.UTF8); }
  
  public byte[] getData() { return data; }
  
  public void   setData(String xhtml) { 
    this.data = xhtml.getBytes(StringUtil.UTF8); 
  }
  
  public void   setData(byte[] data) { 
    this.data = data; 
  }

  public String getContentType() { return contentType; }
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void addTag(String tag) { this.tag = StringUtil.merge(this.tag, tag) ; }
  public void addTag(String[] tag) { this.tag = StringUtil.merge(this.tag, tag) ; }

  public void addTag(String prefix, String[] tag) {
    if(tag == null) return ;
    String[] newTag = new String[tag.length] ;
    for(int i = 0; i < tag.length; i++) newTag[i] = prefix +  tag[i] ;
    this.tag = StringUtil.merge(this.tag, newTag) ;
  }

  public boolean hasTag(String tag) { return StringUtil.isIn(tag, this.tag) ; }

  public String[] getTags() { return tag ; }
  public void     setTags(String[] tag) { this.tag = tag ; }


  public Map<String, Object> attrs() { return attrs; }

  public <T> T attr(String name) { return (T)attrs.get(name) ; }

  public <T> void attr(String name, T value) {
    attrs.put(name, value);
  }

  public Document createJsoupDocument() { return JSoupParser.INSTANCE.parse(getDataAsXhtml());}
}