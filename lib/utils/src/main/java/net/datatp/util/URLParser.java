package net.datatp.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import net.datatp.util.text.StringUtil;

public class URLParser {
  private Pattern PARAM_SEPARATOR = Pattern.compile("&amp;|&") ;

  private String url ;

  private String protocol ;
  private String host ;
  private String port = "80";
  private String path ;
  private String ref;
  private String ext ;

  private TreeMap<String, String[]> params ;

  public URLParser(String url) {
    this.url = url ;
    String string = url.trim() ;

    int idx = string.indexOf("://") ;
    if(idx > 0) {
      this.protocol = string.substring(0, idx + 3);
      string = string.substring(idx + 3, string.length());
    }

    idx = endHostName(string) ;
    if(idx > 0) {
      parseHostPort(string.substring(0, idx)) ;
      string = string.substring(idx, string.length()) ;
    } else {
      int questionMark = string.indexOf('?') ;
      if(questionMark > 0) {
        parseHostPort(string.substring(0, questionMark)) ;
        string = string.substring(questionMark + 1, string.length()) ;
      } else {
        parseHostPort(string) ;
        string = null ;
      }
    }
    if(string == null) return  ;
    int refIndex = string.indexOf('#') ;
    if(refIndex > 0) {
      this.ref = string.substring(refIndex + 1) ;
      string = string.substring(0, refIndex) ;
    }

    int questionMark = string.indexOf('?') ;
    if(questionMark > 0) {
      this.path = string.substring(0, questionMark) ;
      string = string.substring(questionMark + 1, string.length()) ;
    } else {
      this.path = string ;
      string = null ;
    }
    if(string == null) return  ;
    parseParams(string) ;
  }

  public String getURL() { return this.url ; }

  public String getProtocol() { return protocol ; }
  public String getHost() { return host ; }
  public String getPort() { return port ; }
  public String getPath() { 
    if(path == null || path.isEmpty()) return "/" ;
    return this.path ; 
  }
  public String getRef() { return ref; }

  public TreeMap<String, String[]> getParams() { return this.params ; }

  public String getNormalizeHostName() {
    if(host.startsWith("www.")) return host.substring("www.".length()) ; 
    return host ;
  }

  public String getSiteURL() {
    StringBuilder b = new StringBuilder() ;
    b.append(this.protocol).append(this.host) ;

    if(this.port != null && !"80".equals(this.port)) {
      b.append(':').append(this.port) ;
    }
    return b.toString() ;
  }

  public String getPathURL() {
    StringBuilder b = new StringBuilder() ;
    b.append(this.protocol).append(this.host) ;

    if(this.port != null && !"80".equals(this.port)) {
      b.append(':').append(this.port) ;
    }
    if(path != null) b.append(path);
    return b.toString() ;
  }

  public String getBaseURL() {
    StringBuilder b = new StringBuilder() ;
    b.append(this.protocol).append(this.host) ;

    if(this.port != null && !"80".equals(this.port)) {
      b.append(':').append(this.port) ;
    }
    if(path != null && path.length() > 1) {
      int idx = path.lastIndexOf('/') ;
      if(idx >= 0) b.append(path.substring(0, idx));
    }
    return b.toString() ;
  }

  public String getPathWithParams() {
    StringBuilder b = new StringBuilder() ;
    if(path == null || path.isEmpty()) b.append("/") ;
    else b.append(path) ;
    if(params != null) {
      b.append('?') ;
      Iterator<Map.Entry<String, String[]>> i = params.entrySet().iterator() ;
      boolean firstParam = true ;
      while(i.hasNext()) {
        Map.Entry<String, String[]> entry = i.next() ;
        for(String value : entry.getValue()) {
          if(!firstParam) b.append("&") ;
          b.append(entry.getKey()).append('=').append(value) ;
          firstParam = false; 
        }
      }
    }
    //if(ref != null) b.append("#").append(ref) ;
    return b.toString() ;
  }

  public String getNormalizeURL() {
    StringBuilder b = new StringBuilder() ;
    b.append(getPathURL()) ;
    if(params != null) {
      b.append('?') ;
      Iterator<Map.Entry<String, String[]>> i = params.entrySet().iterator() ;
      boolean firstParam = true ;
      while(i.hasNext()) {
        Map.Entry<String, String[]> entry = i.next() ;
        if(entry.getValue() != null) {
          for(String value : entry.getValue()) {
            if(!firstParam) b.append("&") ;
            b.append(entry.getKey()).append('=').append(value) ;
            firstParam = false; 
          }
        } else {
          b.append(entry.getKey()) ;
        }
      }
    }
    return b.toString() ;
  }

  public String getNormalizeURLAll() {
    if(ref != null) {
      StringBuilder b = new StringBuilder() ;
      b.append(getNormalizeURL()) ;
      b.append("#").append(ref) ;
      return b.toString() ;
    } else {
      return getNormalizeURL() ;
    }
  }

  final public String[] getSources() { return getDomains(host) ; }

  public String[] getParamKeys() {
    if(params == null) return StringUtil.EMPTY_ARRAY ;
    return params.keySet().toArray(new String[params.size()]) ;
  }

  public String getHostMD5Id() {
    String host = getNormalizeHostName() ;
    StringBuilder b = new StringBuilder() ;
    b.append(this.protocol).append(host) ;

    if(this.port != null && !"80".equals(this.port)) {
      b.append(':').append(this.port) ;
    }
    if(path != null) b.append(path);
    if(params != null) {
      b.append('?') ;
      Iterator<Map.Entry<String, String[]>> i = params.entrySet().iterator() ;
      boolean firstParam = true ;
      while(i.hasNext()) {
        Map.Entry<String, String[]> entry = i.next() ;
        for(String value : entry.getValue()) {
          if(!firstParam) b.append("&") ;
          b.append(entry.getKey()).append('=').append(value) ;
          firstParam = false; 
        }
      }
    }
    MD5 md5 = MD5.digest(b.toString().toLowerCase()) ;
    return host + ":" + md5.toString();
  }

  public String getExtension() {
    if(this.ext == null) {
      if(path != null) {
        int idx = path.lastIndexOf('.') ;
        if(idx > 0) ext = path.substring(idx) ;
        else ext = "" ;
      } else {
        ext = "" ;
      }
    }
    return ext;
  }

  public List<String> getPathSegments() {
    return StringUtil.split(getPath(), '/') ;
  }

  public void process(URLNormalizerProcessor[] processor) {
    for(URLNormalizerProcessor sel : processor) sel.process(this) ;
  }

  private void parseHostPort(String string) {
    string = string.toLowerCase() ;
    int idx = string.indexOf(':') ;
    if(idx > 0) {
      this.port = string.substring(idx + 1, string.length()) ;
      string = string.substring(0, idx) ;
    }
    if(string.startsWith("www")) {
      if(string.length() > 3 && string.charAt(3) != '.') {
        idx = string.indexOf('.') ;
        string = "www." + string.substring(idx + 1, string.length()) ;
      }
    }
    this.host = string ;
  }

  private void parseParams(String string) {
    String[] nameValue = PARAM_SEPARATOR.split(string) ;
    for(int i = 0; i < nameValue.length; i++) {
      int idx = nameValue[i].indexOf('=') ;
      if(idx < 0) {
        addParam(nameValue[i], null) ;
      } else {
        String name = nameValue[i].substring(0, idx).trim() ;
        String value = nameValue[i].substring(idx + 1, nameValue[i].length()).trim() ;
        addParam(name, value) ;
      }
    }
  }

  private void addParam(String name, String value) {
    if(params == null) params = new TreeMap<String, String[]>() ;
    String[] pvalue = params.get(name) ;
    if(pvalue == null) {
      params.put(name, new String[] { value }) ;
    } else {
      String[] newArray = new String[pvalue.length + 1] ;
      for(int i = 0; i < pvalue.length; i++) {
        newArray[i] = pvalue[i] ;
      }
      newArray[pvalue.length] = value ;
      params.put(name, newArray) ;
    }
  }

  private int endHostName(String string) {
    for(int i = 0; i < string.length(); i++) {
      char c = string.charAt(i) ;
      if(c == '/' || c == '?') return i ;
    }
    return string.length() ;
  }

  final static public String[] getDomains(String host) {
    List<String> holder = new ArrayList<String>() ;
    String source = host ;
    holder.add(source) ;
    int idx = source.indexOf('.') ;
    while(idx >= 0) {
      source = source.substring(idx + 1) ;
      if(source.indexOf('.') < 0) break ;
      holder.add(source) ;
      idx = source.indexOf('.') ;
    }
    holder.add(source) ;
    return holder.toArray(new String[holder.size()]);
  }
}