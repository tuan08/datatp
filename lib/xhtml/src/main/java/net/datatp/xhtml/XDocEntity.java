package net.datatp.xhtml;

import java.util.LinkedHashMap;
import java.util.Map;

import net.datatp.util.text.StringUtil;

public class XDocEntity extends LinkedHashMap<String, String[]>  {
  private static final long serialVersionUID = 1L;
  
  final static public String NAME = "name";
  final static public String TYPE = "type";
  final static public String TAG  = "tag";
  
  public XDocEntity() {}
  
  public XDocEntity(String name, String type) {
    withName(name);
    withType(type);
  }
  
  public String name() { return fieldAsString(NAME); }
  public void withName(String name) { field(NAME, name); }
  
  public String type() { return fieldAsString(NAME); }
  public void   withType(String type) { field(TYPE, type); }

  public void addTag(String tag) {
    String[] mergedTag = StringUtil.merge(field(TAG), tag) ;
    field(TAG, mergedTag);
  }

  public void addTag(String[] tag) {
    String[] mergedTag = StringUtil.merge(field(TAG), tag) ;
    field(TAG, mergedTag);
  }

  public void addTag(String prefix, String[] tag) {
    if(tag == null) return ;
    String[] newTag = new String[tag.length] ;
    for(int i = 0; i < tag.length; i++) {
      newTag[i] = prefix +  tag[i] ;
    }
    String[] mergedTag = StringUtil.merge(field(TAG), newTag) ;
    field(TAG, mergedTag);
  }

  public boolean hasTag(String tag) { 
    String[] tags = field(TAG);
    return StringUtil.isIn(tag, tags) ; 
  }

  public String[] tags() { return field(TAG) ; }
  public void     withTags(String[] tag) { field(TAG, tag) ; }

  public String[] field(String name) { return get(name);}
  
  public String   fieldAsString(String name) { 
    String[] value = get(name);
    if(value != null) {
      if(value.length == 1) return value[0];
      return StringUtil.joinStringArray(value);
    }
    return null;
  }
  
  public void field(String name, String[] value) { put(name, value); }
  public void field(String name, String value) { put(name, new String[] { value }); }
  
  public String getFormattedText() {
    StringBuilder b = new StringBuilder();
    for(Map.Entry<String, String[]> entry : entrySet()) {
      b.append(entry.getKey()).append(": ").append(StringUtil.joinStringArray(entry.getValue())).append("\n");
    }
    return b.toString();
  }
  
}
