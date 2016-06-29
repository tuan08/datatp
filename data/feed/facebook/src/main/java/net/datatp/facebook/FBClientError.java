package net.datatp.facebook;

import com.restfb.exception.FacebookGraphException;

public class FBClientError {
  static public enum Type { token, object, permission, unknown }
  
  private Type    type ;
  private boolean ignorable = false;
  
  public FBClientError(Type type, boolean ignorable) {
    this.type      = type;
    this.ignorable = ignorable;
  }
  
  public Type getType() { return this.type; }
  
  public boolean isIgnorable() { return this.ignorable; }
  
  static public FBClientError getFBClientError(FacebookGraphException ex) {
    if(ex.getErrorCode() == 100) {
      if(ex.getMessage().contains("does not exist")) return new FBClientError(Type.object, true);
    }
    return new FBClientError(Type.unknown, false);
  }
}
