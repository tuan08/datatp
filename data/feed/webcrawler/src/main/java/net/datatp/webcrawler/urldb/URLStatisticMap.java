package net.datatp.webcrawler.urldb;

import java.io.Serializable;

import net.datatp.util.stat.Statistics;
import net.datatp.webcrawler.ErrorCode;
import net.datatp.webcrawler.ResponseCode;


public class URLStatisticMap implements Serializable {
  final static public String FETCH_STATUS  = "fetchStatus";
  final static public String RESPONSE_CODE = "responseCode";
  final static public String ERROR_CODE    = "errorCode";
  final static public String PAGE_TYPES    = "pageTypes";
  final static public String FETCH_COUNT   = "fetchCount";
  
  private String name;
  private Statistics readonly = new Statistics() ;
  private Statistics writable = new Statistics() ;

  public URLStatisticMap() {
    init(readonly) ;
    init(writable) ;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Statistics getStatisticMap() { return readonly ; }

  public void log(URLDatum urldatum) {
    logStatus(urldatum) ;
    logResponseCode(urldatum) ;
    logError(urldatum) ;
    logPageType(urldatum) ;
    logFetchCount(urldatum) ;
  }

  public void onPostPreSchedule() {
    readonly = writable ;
    writable = new Statistics() ;
    init(writable) ;
  }

  private void logStatus(URLDatum urldatum) {
    writable.incr(FETCH_STATUS, "All", 1) ;
    if (urldatum.getRedirectUrl().getLength() != 0) {
      writable.incr(FETCH_STATUS, "wRedirect", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_FETCHING) {
      writable.incr(FETCH_STATUS, "Pending", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_WAITING) {
      writable.incr(FETCH_STATUS, "Waiting", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_NEW) {
      writable.incr(FETCH_STATUS, "New", 1) ;
    } else {
      writable.incr(FETCH_STATUS, "Other", 1) ;
    }
  }

  private void logResponseCode(URLDatum datum) {
    writable.incr(RESPONSE_CODE, "All", 1) ;
    long responseCode = datum.getLastResponseCode() ;
    if(responseCode == ResponseCode.NONE) {
      writable.incr(RESPONSE_CODE, "NONE", 1) ;
    } else if(responseCode == 200) {
      writable.incr(RESPONSE_CODE, "OK", 1) ;
    } else if(responseCode > 0 && responseCode < 200) {
      writable.incr(RESPONSE_CODE, "RC100", 1) ;
    } else if(responseCode > 200 && responseCode < 300) {
      writable.incr(RESPONSE_CODE, "RC200", 1) ;
    } else if(responseCode >= 300 && responseCode < 400) {
      writable.incr(RESPONSE_CODE, "RC300", 1) ;
    } else if(responseCode >= 400 && responseCode < 500) {
      writable.incr(RESPONSE_CODE, "RC400", 1) ;
    } else if(responseCode >= 500 && responseCode < 600) {
      writable.incr(RESPONSE_CODE, "RC500", 1) ;
    } else {
      writable.incr(RESPONSE_CODE, "Unknown", 1) ;
    }
  }

  private void logError(URLDatum datum) {
    writable.incr(ERROR_CODE, "All", 1) ;
    short ec = datum.getLastErrorCode() ;
    if(ec == ErrorCode.ERROR_TYPE_NONE) {
      writable.incr(ERROR_CODE, "None",1);
    } else if(ec == ErrorCode.ERROR_CONNECTION) {
      writable.incr(ERROR_CODE, "Error Conn", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_NOT_AUTHORIZED) {
      writable.incr(ERROR_CODE, "Conn not Auth", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_SOCKET_TIMEOUT) {
      writable.incr(ERROR_CODE, "Conn socket timout", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_TIMEOUT) {
      writable.incr(ERROR_CODE, "Conn timeout", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_UNKNOWN_HOST) {
      writable.incr(ERROR_CODE, "Conn unknown host", 1);
    } else if(ec == ErrorCode.ERROR_DB_CONFIG_GET) {
      writable.incr(ERROR_CODE, "DB config get", 1) ;
    } else if(ec == ErrorCode.ERROR_DB_CONFIG_NOT_FOUND) {
      writable.incr(ERROR_CODE, "DB config not found", 1);
    } else if(ec == ErrorCode.ERROR_UNKNOWN) {
      writable.incr(ERROR_CODE, "Uknown", 1);
    }
  }

  private void logPageType(URLDatum datum) {
    byte type = datum.getPageType() ;
    writable.incr(PAGE_TYPES, "All", 1) ;
    if(type == URLDatum.PAGE_TYPE_LIST) {
      writable.incr(PAGE_TYPES, "List", 1) ;
    } else if(type == URLDatum.PAGE_TYPE_DETAIL) {
      writable.incr(PAGE_TYPES, "Detail", 1) ;
    } else {
      writable.incr(PAGE_TYPES, "Other", 1) ;
    }
  }

  private void logFetchCount(URLDatum datum) {
    int count = datum.getFetchCount() ;
    writable.incr(FETCH_COUNT, "All", 1) ;
    if(count == 0) {
      writable.incr(FETCH_COUNT, "FC0", 1) ;
    } else if ((count >= 1) && (count < 5)) {
      writable.incr(FETCH_COUNT, "FC1-5", 1) ;
    } else if ((count >= 5) && (count < 10)) {
      writable.incr(FETCH_COUNT, "FC5-10", 1) ;
    } else if ((count >= 10) && (count < 25)) {
      writable.incr(FETCH_COUNT, "FC10-25", 1) ;
    } else {
      writable.incr(FETCH_COUNT, "FC>25", 1) ;
    }
  }

  private void init(Statistics map) {
    map.incr(FETCH_STATUS, "All", 0) ;
    map.incr(FETCH_STATUS, "Pending", 0) ;
    map.incr(FETCH_STATUS, "Waiting", 0) ;
    map.incr(FETCH_STATUS, "New", 0) ;
    map.incr(FETCH_STATUS, "wRedirect", 0) ;
    map.incr(FETCH_STATUS, "Other", 0) ;

    map.incr(RESPONSE_CODE, "All", 0) ;
    map.incr(RESPONSE_CODE, "NONE", 0) ;
    map.incr(RESPONSE_CODE, "OK", 0) ;
    map.incr(RESPONSE_CODE, "RC100", 0) ;
    map.incr(RESPONSE_CODE, "RC200", 0) ;
    map.incr(RESPONSE_CODE, "RC300", 0) ;
    map.incr(RESPONSE_CODE, "RC400", 0) ;
    map.incr(RESPONSE_CODE, "RC500", 0) ;
    map.incr(RESPONSE_CODE, "Unknown", 0) ;

    map.incr(ERROR_CODE, "All", 0) ;
    map.incr(ERROR_CODE, "None", 0) ;
    map.incr(ERROR_CODE, "Error Conn", 0) ;
    map.incr(ERROR_CODE, "Conn not Auth", 0) ;
    map.incr(ERROR_CODE, "Conn socket timout", 0) ;
    map.incr(ERROR_CODE, "Conn timeout", 0) ;
    map.incr(ERROR_CODE, "Conn unknown host", 0);
    map.incr(ERROR_CODE, "DB config get", 0) ;
    map.incr(ERROR_CODE, "DB config not found", 0);
    map.incr(ERROR_CODE, "Uknown",0);

    map.incr(PAGE_TYPES, "All", 0) ;
    map.incr(PAGE_TYPES, "List", 0) ;
    map.incr(PAGE_TYPES, "Detail", 0) ;
    map.incr(PAGE_TYPES, "Other", 0) ;

    map.incr(FETCH_COUNT, "All", 0) ;
    map.incr(FETCH_COUNT, "FC0", 0) ;
    map.incr(FETCH_COUNT, "FC1-5", 0) ;
    map.incr(FETCH_COUNT, "FC5-10", 0) ;
    map.incr(FETCH_COUNT, "FC10-25", 0) ;
    map.incr(FETCH_COUNT, "FC>25", 0) ;
  }
}