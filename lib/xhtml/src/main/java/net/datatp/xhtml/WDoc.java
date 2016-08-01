package net.datatp.xhtml;

import java.io.Serializable;
import java.util.Map;

import net.datatp.util.text.StringUtil;

public class WDoc implements Serializable {
  private static final long serialVersionUID = 1L;

  private String                type;
  private String                data;
  private Map<String, String>   attr;
  private Map<String, Entity>   entities;
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public String getData() { return data; }
  public void setData(String data) { this.data = data; }

  public Map<String, String> getAttr() { return attr; }
  public void setAttr(Map<String, String> attr) { this.attr = attr; }

  public Map<String, Entity> getEntities() { return entities; }
  public void setEntities(Map<String, Entity> entities) { this.entities = entities; }

  static public class Entity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String   name;
    private String[] tag;
    private String   content;
    
    public Entity() {}
    
    public Entity(String name, String[] tag) { this(name, tag, null); }
    
    public Entity(String name, String[] tag, String content) {
      this.name    = name;
      this.tag     = tag;
      this.content = content;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String[] getTag() { return tag ; }
    public void setTag(String[] tag) { this.tag = tag ; }
    
    public void addTag(String tag) { this.tag = StringUtil.merge(this.tag, tag); }
    public boolean hasTag(String tag) { return StringUtil.isIn(tag, this.tag) ; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
  }
}
