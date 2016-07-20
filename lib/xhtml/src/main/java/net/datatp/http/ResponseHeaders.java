package net.datatp.http;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders implements Serializable {
  private Map<String, String> headers = new HashMap<>();

  public String getHeader(String name, String defaultVal) {
    String val = headers.get(name);
    if(val == null) return defaultVal;
    return val;
  }
  
  public int getHeaderAsInt(String name, int defaultVal) {
    String val = headers.get(name);
    if(val == null) return defaultVal;
    return Integer.parseInt(val);
  }
  
  public void setHeader(String name, String value) {
    headers.put(name, value);
  }
  
  public int getResponseCode() { return this.getHeaderAsInt("response-code", 600); }
  public void setResponseCode(int val) { setHeader("response-code", Integer.toString(val)); }
}
