package net.datatp.nlp.dict;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.datatp.nlp.util.StringPool;
import net.datatp.util.MD5;
import net.datatp.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Meaning extends HashMap<String, Object>  {
  private static final long serialVersionUID = 1L;

  final static public String OTYPE        = "otype" ;
  final static public String LANG         = "lang";
  final static public String NAME         = "name" ;
  final static public String VARIANT      = "variant" ;
  final static public String TOPIC        = "topic" ;
  final static public String UDESCRIPTION = "udescription" ;

  @JsonIgnore
  public String getId() {
    String uid = getUDescription() ;
    if(uid == null) uid = getTopic() ;
    if(uid == null) uid = getName() ;
    return MD5.digest(uid).toString() ; 
  }

  public String getOType() { return (String)get("otype") ; }
  public void   setOType(String type) { put("otype", type) ;}

  public String getLang() { return (String)get("lang") ; }
  public void   setLang(String lang){ put("lang", lang) ;}

  public String   getName() { return (String) get(NAME) ; }
  public void     setName(String name) { 
    if(name == null || name.length() == 0) remove(NAME) ;
    else put(NAME, name) ;
  }

  public String[] getVariant() { 
    try { 
      return (String[]) get(VARIANT) ;
    } catch(Throwable t) {
      System.out.println("Cast Exception for " + getName());
      return null ;
    }
  }
  public void setVariant(String[] variant) { put(VARIANT, variant) ; }
  public void setVariants(String variants) { 
    String[] variant = StringUtil.splitAsArray(variants, ',');
    put(VARIANT, variant) ; 
  }

  public String   getTopic() { return (String) get(TOPIC) ; }
  public void     setTopic(String topic) { 
    if(topic == null || topic.length() == 0) remove(TOPIC) ;
    else put(TOPIC, topic) ;
  }

  public String getUDescription() { return (String) get(UDESCRIPTION); }
  public void   setUDescription(String string) { put(UDESCRIPTION, string) ; }

  public String[] getType() { return (String[]) get("type") ; }
  public void     setType(String[] type) { put("type", type) ;}

  public String[] getStringArray(String name) {
    Object value = this.get(name) ;
    if(value == null) return null ;
    if(value instanceof String[]) return (String[]) value ;
    if(value instanceof String) return (String[]) new String[] {(String)value} ;
    return (String[]) new String[] {value.toString() } ;
  }

  public String getString(String name) {
    Object value = this.get(name) ;
    if(value == null) return null ;
    return (String) value ;
  }

  public Object put(String key, Object value) {
    if(value == null) {
      remove(key) ;
      return value ;
    } else if(value instanceof String) {
      String string = ((String) value).trim();
      if(string.startsWith("[") && string.endsWith("]")) {
        string = string.substring(1, string.length() - 1);
        put(key, StringUtil.splitAsArray(string, ','));
      }
    } else if(value instanceof List) {
      List<String> list = (List<String>) value ;
      value = list.toArray(new String[list.size()]) ;
    }
    return super.put(key, value) ;
  }

  public void optimize(StringPool pool) {
    Iterator<String> i = keySet().iterator() ;
    while(i.hasNext()) {
      String key = i.next() ;
      Object value = get(key) ;
      key = pool.getString(key) ;
      if(value instanceof String[]) {
        value = pool.getString((String[])value) ;
      } else {
        value = pool.getString((String)value) ;
      }
      put(key, value) ;
    }
  }
}