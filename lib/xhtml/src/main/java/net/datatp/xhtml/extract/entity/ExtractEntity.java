package net.datatp.xhtml.extract.entity;

import net.datatp.util.text.StringUtil;

public class ExtractEntity {
  private String   type;
  private String[] tag;
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

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

}
