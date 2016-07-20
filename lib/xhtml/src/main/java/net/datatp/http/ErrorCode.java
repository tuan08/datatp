package net.datatp.http;

import java.util.HashMap;

/**
 * For more detail about the http response code http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jun 30, 2010  
 */
public class ErrorCode {
  final static public byte ERROR_TYPE_NONE                 = 0 ;
  
  final static public byte ERROR_DB_CONFIG_NOT_FOUND       = 1 ;
  final static public byte ERROR_DB_CONFIG_GET             = 2 ;
  
  final static public byte ERROR_CONNECTION                = 10 ;
  final static public byte ERROR_CONNECTION_TIMEOUT        = 11 ;
  final static public byte ERROR_CONNECTION_SOCKET_TIMEOUT = 12 ;
  final static public byte ERROR_CONNECTION_NOT_AUTHORIZED = 13 ;
  final static public byte ERROR_CONNECTION_UNKNOWN_HOST   = 14 ;
  
  final static public byte ERROR_UNKNOWN                   = 127 ;
  
  static HashMap<Byte, ErrorCode> codes = new HashMap<Byte, ErrorCode>() ;
  static {
    addResponseCode(ERROR_TYPE_NONE, "error code none") ;
    addResponseCode(ERROR_DB_CONFIG_NOT_FOUND, "Cannot find the configuration for a given url") ;
    addResponseCode(ERROR_DB_CONFIG_GET, "Cannot connect to the configuration database") ;
  }
  
  final public  byte code ;
  final public  String description ;
  
  private ErrorCode(byte code, String desc) {
    this.code = code ;
    this.description = desc; 
  }

  final static public ErrorCode get(byte code) {
    ErrorCode rc =  codes.get(code) ;
    if(rc == null) {
      rc = new ErrorCode(code, "unknow response code") ;
      codes.put(code, rc) ;
    }
    return rc ;
  }
  
  static private void addResponseCode(byte code, String desc) {
    codes.put(code, new ErrorCode(code, desc)) ;
  }
}
