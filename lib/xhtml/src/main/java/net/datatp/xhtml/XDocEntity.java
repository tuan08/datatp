package net.datatp.xhtml;

import java.io.Serializable;
import java.util.List;

import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.extract.entity.ExtractEntity;

abstract public class XDocEntity implements Serializable  {
  private static final long serialVersionUID = 1L;
  
  private String   name;
  private String   type;
  private String[] tag;
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name;}
  
  public String getType() { return type; }
  public void   setType(String type) { this.type = type; }

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

  abstract public String getFormattedText() ;
  
  static public String toString(List<ExtractEntity> holder) {
    StringBuilder b = new StringBuilder();
    for(XDocEntity sel : holder) {
      b.append(sel.getFormattedText()).append("\n");
    }
    return b.toString();
  }
}
