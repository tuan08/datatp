package net.datatp.webcrawler.urldb;

import java.io.Serializable;

import net.datatp.util.stat.StatisticsSet;
import net.datatp.webcrawler.ErrorCode;
import net.datatp.webcrawler.ResponseCode;


public class URLDatumStatisticMap implements Serializable {
  private String name;
  private StatisticsSet readonly = new StatisticsSet() ;
  private StatisticsSet writable = new StatisticsSet() ;

  public URLDatumStatisticMap() {
    init(readonly) ;
    init(writable) ;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public StatisticsSet getStatisticMap() { return readonly ; }

  public void log(URLDatum urldatum) {
    logStatus(urldatum) ;
    logResponseCode(urldatum) ;
    logError(urldatum) ;
    logPageType(urldatum) ;
    logFetchCount(urldatum) ;
  }

  public void onPostPreSchedule() {
    readonly = writable ;
    writable = new StatisticsSet() ;
    init(writable) ;
  }

  private void logStatus(URLDatum urldatum) {
    writable.incr("Fetch Status", "All", 1) ;
    if (urldatum.getRedirectUrl().getLength() != 0) {
      writable.incr("Fetch Status", "wRedirect", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_FETCHING) {
      writable.incr("Fetch Status", "Pending", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_WAITING) {
      writable.incr("Fetch Status", "Waiting", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_NEW) {
      writable.incr("Fetch Status", "New", 1) ;
    } else {
      writable.incr("Fetch Status", "Other", 1) ;
    }
  }

  private void logResponseCode(URLDatum datum) {
    writable.incr("Response Code", "All", 1) ;
    long responseCode = datum.getLastResponseCode() ;
    if(responseCode == ResponseCode.NONE) {
      writable.incr("Response Code", "NONE", 1) ;
    } else if(responseCode == 200) {
      writable.incr("Response Code", "OK", 1) ;
    } else if(responseCode > 0 && responseCode < 200) {
      writable.incr("Response Code", "RC100", 1) ;
    } else if(responseCode > 200 && responseCode < 300) {
      writable.incr("Response Code", "RC200", 1) ;
    } else if(responseCode >= 300 && responseCode < 400) {
      writable.incr("Response Code", "RC300", 1) ;
    } else if(responseCode >= 400 && responseCode < 500) {
      writable.incr("Response Code", "RC400", 1) ;
    } else if(responseCode >= 500 && responseCode < 600) {
      writable.incr("Response Code", "RC500", 1) ;
    } else {
      writable.incr("Response Code", "Unknown", 1) ;
    }
  }

  private void logError(URLDatum datum) {
    writable.incr("Error Code", "All", 1) ;
    short ec = datum.getLastErrorCode() ;
    if(ec == ErrorCode.ERROR_TYPE_NONE) {
      writable.incr("Error Code", "None",1);
    } else if(ec == ErrorCode.ERROR_CONNECTION) {
      writable.incr("Error Code", "Error Conn", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_NOT_AUTHORIZED) {
      writable.incr("Error Code", "Conn not Auth", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_SOCKET_TIMEOUT) {
      writable.incr("Error Code", "Conn socket timout", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_TIMEOUT) {
      writable.incr("Error Code", "Conn timeout", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_UNKNOWN_HOST) {
      writable.incr("Error Code", "Conn unknown host", 1);
    } else if(ec == ErrorCode.ERROR_DB_CONFIG_GET) {
      writable.incr("Error Code", "DB config get", 1) ;
    } else if(ec == ErrorCode.ERROR_DB_CONFIG_NOT_FOUND) {
      writable.incr("Error Code", "DB config not found", 1);
    } else if(ec == ErrorCode.ERROR_UNKNOWN) {
      writable.incr("Error Code", "Uknown", 1);
    }
  }

  private void logPageType(URLDatum datum) {
    byte type = datum.getPageType() ;
    writable.incr("Page Types", "All", 1) ;
    if(type == URLDatum.PAGE_TYPE_LIST) {
      writable.incr("Page Types", "List", 1) ;
    } else if(type == URLDatum.PAGE_TYPE_DETAIL) {
      writable.incr("Page Types", "Detail", 1) ;
    } else {
      writable.incr("Page Types", "Other", 1) ;
    }
  }

  //TODO:
  private void logFetchCount(URLDatum datum) {
    int count = datum.getFetchCount() ;
    writable.incr("Fetch Count", "All", 1) ;
    if(count == 0) {
      writable.incr("Fetch Count", "FC0", 1) ;
    } else if ((count >= 1) && (count < 5)) {
      writable.incr("Fetch Count", "FC1-5", 1) ;
    } else if ((count >= 5) && (count < 10)) {
      writable.incr("Fetch Count", "FC5-10", 1) ;
    } else if ((count >= 10) && (count < 25)) {
      writable.incr("Fetch Count", "FC10-25", 1) ;
    } else {
      writable.incr("Fetch Count", "FC>25", 1) ;
    }
  }

  private void init(StatisticsSet map) {
    map.incr("Fetch Status", "All", 0) ;
    map.incr("Fetch Status", "Pending", 0) ;
    map.incr("Fetch Status", "Waiting", 0) ;
    map.incr("Fetch Status", "New", 0) ;
    map.incr("Fetch Status", "wRedirect", 0) ;
    map.incr("Fetch Status", "Other", 0) ;

    map.incr("Response Code", "All", 0) ;
    map.incr("Response Code", "NONE", 0) ;
    map.incr("Response Code", "OK", 0) ;
    map.incr("Response Code", "RC100", 0) ;
    map.incr("Response Code", "RC200", 0) ;
    map.incr("Response Code", "RC300", 0) ;
    map.incr("Response Code", "RC400", 0) ;
    map.incr("Response Code", "RC500", 0) ;
    map.incr("Response Code", "Unknown", 0) ;

    map.incr("Error Code", "All", 0) ;
    map.incr("Error Code", "None", 0) ;
    map.incr("Error Code", "Error Conn", 0) ;
    map.incr("Error Code", "Conn not Auth", 0) ;
    map.incr("Error Code", "Conn socket timout", 0) ;
    map.incr("Error Code", "Conn timeout", 0) ;
    map.incr("Error Code", "Conn unknown host", 0);
    map.incr("Error Code", "DB config get", 0) ;
    map.incr("Error Code", "DB config not found", 0);
    map.incr("Error Code", "Uknown",0);

    map.incr("Page Types", "All", 0) ;
    map.incr("Page Types", "List", 0) ;
    map.incr("Page Types", "Detail", 0) ;
    map.incr("Page Types", "Other", 0) ;

    map.incr("Fetch Count", "All", 0) ;
    map.incr("Fetch Count", "FC0", 0) ;
    map.incr("Fetch Count", "FC1-5", 0) ;
    map.incr("Fetch Count", "FC5-10", 0) ;
    map.incr("Fetch Count", "FC10-25", 0) ;
    map.incr("Fetch Count", "FC>25", 0) ;
  }
}