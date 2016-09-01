package net.datatp.xhtml;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class XDoc implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
  private Date                    timestamp;

  private String                  type;
  private String                  data;
  private Map<String, String>     attrs;
  private Map<String, XDocEntity> entities;

  public Date getTimestamp() { return timestamp; }
  public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public String getData() { return data; }
  public void   setData(String data) { this.data = data; }
  
  public String attr(String name) {
    if(attrs == null) return null;
    return attrs.get(name);
  }
  
  public void attr(String name, String val) {
    if(attrs == null) attrs = new HashMap<>();
    attrs.put(name, val);
  }
  
  @JsonProperty("attr")
  public Map<String, String> getAttrs() { return attrs; }
  public void setAttrs(Map<String, String> attrs) { this.attrs = attrs; }
  
  @JsonProperty("entity")
  public Map<String, XDocEntity> getEntities() { return entities;  }
  public void setEntities(Map<String, XDocEntity> entities) { this.entities = entities; }
  
  
  public void addEntity(XDocEntity entity) {
    if(entities == null) entities = new HashMap<>();
    entities.put(entity.name(), entity);
  }
}
