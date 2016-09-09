package net.datatp.crawler.site;

import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.http.ResponseCode;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.util.stat.Statistics;

public class SiteStatistic {
  final static public String FETCH_STATUS  = "fetchStatus";
  final static public String RESPONSE_CODE = "responseCode";
  final static public String ERROR_CODE    = "errorCode";
  final static public String PAGE_TYPES    = "pageTypes";
  final static public String FETCH_COUNT   = "fetchCount";

  private String               group;
  private String               hostname;
  private int                  scheduleCount;
  private int                  commitCount;
  private Statistics           urlStatistics = new Statistics();
 
  transient private Statistics writable      = new Statistics();

  public SiteStatistic() {
    init(urlStatistics) ;
    init(writable) ;
  }
  
  public SiteStatistic(String group, String hostname) {
    this();
    this.group    = group;
    this.hostname = hostname;
  }

  public String getGroup() { return group; }
  public void setGroup(String group) { this.group = group; }

  public String getHostname() { return hostname; }
  public void setHostname(String hostname) { this.hostname = hostname; }

  public Statistics getUrlStatistics() { return urlStatistics; }
  public void setUrlStatistics(Statistics statistics) { this.urlStatistics = statistics; }

  public int  getScheduleCount() { return this.scheduleCount ; }
  public void setScheduleCount(int count) { this.scheduleCount = count; }
  
  public int  getCommitCount() { return commitCount ; }
  public void setCommitCount(int count) { this.commitCount = count; }
  
  public int  getInQueue() { return scheduleCount - commitCount ; }
  public void setInQueue(int num) {  }
  
  public void addScheduleCount(int num) {  this.scheduleCount +=  num ; }
  
  public boolean canSchedule(int maxSchedulePerSite, int maxConn) {
    maxSchedulePerSite = maxSchedulePerSite * maxConn ;
    if(scheduleCount - commitCount < maxSchedulePerSite) return true ;
    return false ;
  }
  
  public int getMaxSchedule(int maxSchedulePerSite, int maxConn) {
    maxSchedulePerSite = maxSchedulePerSite * maxConn ;
    int inqueue = scheduleCount - commitCount ;
    int ret = maxSchedulePerSite - inqueue ;
    return ret ;
  }
  
  public void addCommitCount(int num) { commitCount +=  num ; }
  
  public void log(URLDatum urldatum) {
    logStatus(urldatum) ;
    logResponseCode(urldatum) ;
    logError(urldatum) ;
    logPageType(urldatum) ;
    logFetchCount(urldatum) ;
  }

  public void onPostPreSchedule() {
    urlStatistics = writable ;
    writable = new Statistics() ;
    init(writable) ;
  }

  private void logStatus(URLDatum urldatum) {
    writable.incr(FETCH_STATUS, "All", 1) ;
    if (urldatum.getRedirectUrl() != null) {
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