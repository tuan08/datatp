package net.datatp.tracking;

import java.util.Properties;

public class PropertiesConfig extends Properties {
  
  public int getPropertyAsInt(String key, int defaultValue) {
    String val = getProperty(key);
    if(val != null) return Integer.parseInt(val);
    return defaultValue;
  }
  
  public long getPropertyAsLong(String key, long defaultValue) {
    String val = getProperty(key);
    if(val != null) return Long.parseLong(val);
    return defaultValue;
  }
  
  public float getPropertyAsFloat(String key, float defaultValue) {
    String val = getProperty(key);
    if(val != null) return Float.parseFloat(val);
    return defaultValue;
  }
  
  public double getPropertyAsDouble(String key, double defaultValue) {
    String val = getProperty(key);
    if(val != null) return Double.parseDouble(val);
    return defaultValue;
  }
  
  public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
    String val = getProperty(key);
    if(val != null) return Boolean.parseBoolean(val);
    return defaultValue;
  }
}
