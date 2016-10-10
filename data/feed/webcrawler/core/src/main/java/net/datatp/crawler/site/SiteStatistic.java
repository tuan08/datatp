package net.datatp.crawler.site;

import java.util.TreeMap;

import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.http.ResponseCode;
import net.datatp.crawler.urldb.URLDatum;

public class SiteStatistic {
  final static public String URL_STATUS    = "urlStatus";
  final static public String RESPONSE_CODE = "responseCode";
  final static public String ERROR_CODE    = "errorCode";
  final static public String PAGE_TYPES    = "pageTypes";
  final static public String FETCH_COUNT   = "fetchCount";

  private String               group;
  private String               hostname;
  private int                  scheduleCount;
  private int                  commitCount;
  
  private TreeMap<String, Statistic> readonly = new TreeMap<>();
  private TreeMap<String, Statistic> statistics = new TreeMap<>();
  
  public SiteStatistic(String group, String hostname) {
    this.group    = group;
    this.hostname = hostname;
  }

  public String getGroup() { return group; }
  public void setGroup(String group) { this.group = group; }

  public String getHostname() { return hostname; }
  public void setHostname(String hostname) { this.hostname = hostname; }

  public int  getScheduleCount() { return this.scheduleCount ; }
  public void setScheduleCount(int count) { this.scheduleCount = count; }
  
  public int  getCommitCount() { return commitCount ; }
  public void setCommitCount(int count) { this.commitCount = count; }
  
  public int  getInQueue() { return scheduleCount - commitCount ; }
  public void setInQueue(int num) {  }
  
  public TreeMap<String, Statistic> getStatistics() { return readonly; }
  
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
    this.readonly = this.statistics ;
    this.statistics = new TreeMap<>() ;
    init() ;
  }

  void incrStat(String category, String name, int count) {
    String key = category + "_" + name;
    Statistic stat = statistics.get(key);
    if(stat == null) {
      synchronized(statistics) {
        stat = new Statistic(category, name);
        statistics.put(key, stat);
      }
    }
    stat.incr(count);
  }
  
  private void logStatus(URLDatum urldatum) {
    incrStat(URL_STATUS, "All", 1) ;
    if (urldatum.getRedirectUrl() != null) {
      incrStat(URL_STATUS, "wRedirect", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_FETCHING) {
      incrStat(URL_STATUS, "Pending", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_WAITING) {
      incrStat(URL_STATUS, "Waiting", 1) ;
    } else if (urldatum.getStatus() == URLDatum.STATUS_NEW) {
      incrStat(URL_STATUS, "New", 1) ;
    } else {
      incrStat(URL_STATUS, "Other", 1) ;
    }
  }

  private void logResponseCode(URLDatum datum) {
    incrStat(RESPONSE_CODE, "All", 1) ;
    long responseCode = datum.getLastResponseCode() ;
    if(responseCode == ResponseCode.NONE) {
      incrStat(RESPONSE_CODE, "NONE", 1) ;
    } else if(responseCode == 200) {
      incrStat(RESPONSE_CODE, "OK", 1) ;
    } else if(responseCode > 0 && responseCode < 200) {
      incrStat(RESPONSE_CODE, "RC100", 1) ;
    } else if(responseCode > 200 && responseCode < 300) {
      incrStat(RESPONSE_CODE, "RC200", 1) ;
    } else if(responseCode >= 300 && responseCode < 400) {
      incrStat(RESPONSE_CODE, "RC300", 1) ;
    } else if(responseCode >= 400 && responseCode < 500) {
      incrStat(RESPONSE_CODE, "RC400", 1) ;
    } else if(responseCode >= 500 && responseCode < 600) {
      incrStat(RESPONSE_CODE, "RC500", 1) ;
    } else {
      incrStat(RESPONSE_CODE, "Unknown", 1) ;
    }
  }

  private void logError(URLDatum datum) {
    short ec = datum.getLastErrorCode() ;
    if(ec == ErrorCode.ERROR_TYPE_NONE) return;
    
    incrStat(ERROR_CODE, "All", 1) ;
    if(ec == ErrorCode.ERROR_CONNECTION) {
      incrStat(ERROR_CODE, "Error Conn", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_NOT_AUTHORIZED) {
      incrStat(ERROR_CODE, "Conn not Auth", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_SOCKET_TIMEOUT) {
      incrStat(ERROR_CODE, "Conn socket timout", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_TIMEOUT) {
      incrStat(ERROR_CODE, "Conn timeout", 1) ;
    } else if(ec == ErrorCode.ERROR_CONNECTION_UNKNOWN_HOST) {
      incrStat(ERROR_CODE, "Conn unknown host", 1);
    } else if(ec == ErrorCode.ERROR_DB_CONFIG_GET) {
      incrStat(ERROR_CODE, "DB config get", 1) ;
    } else if(ec == ErrorCode.ERROR_DB_CONFIG_NOT_FOUND) {
      incrStat(ERROR_CODE, "DB config not found", 1);
    } else if(ec == ErrorCode.ERROR_UNKNOWN) {
      incrStat(ERROR_CODE, "Uknown", 1);
    }
  }

  private void logPageType(URLDatum datum) {
    byte type = datum.getPageType() ;
    incrStat(PAGE_TYPES, "All", 1) ;
    if(type == URLDatum.PAGE_TYPE_LIST) {
      incrStat(PAGE_TYPES, "List", 1) ;
    } else if(type == URLDatum.PAGE_TYPE_DETAIL) {
      incrStat(PAGE_TYPES, "Detail", 1) ;
    } else {
      incrStat(PAGE_TYPES, "Other", 1) ;
    }
  }

  private void logFetchCount(URLDatum datum) {
    int count = datum.getFetchCount() ;
    incrStat(FETCH_COUNT, "All", 1) ;
    if(count == 0) {
      incrStat(FETCH_COUNT, "FC0", 1) ;
    } else if ((count >= 1) && (count < 5)) {
      incrStat(FETCH_COUNT, "FC1-5", 1) ;
    } else if ((count >= 5) && (count < 10)) {
      incrStat(FETCH_COUNT, "FC5-10", 1) ;
    } else if ((count >= 10) && (count < 25)) {
      incrStat(FETCH_COUNT, "FC10-25", 1) ;
    } else {
      incrStat(FETCH_COUNT, "FC>25", 1) ;
    }
  }

  private void init() {
    incrStat(URL_STATUS, "All", 0) ;
    incrStat(URL_STATUS, "Pending", 0) ;
    incrStat(URL_STATUS, "Waiting", 0) ;
    incrStat(URL_STATUS, "New", 0) ;
    incrStat(URL_STATUS, "wRedirect", 0) ;
    incrStat(URL_STATUS, "Other", 0) ;

    incrStat(RESPONSE_CODE, "All", 0) ;
    incrStat(RESPONSE_CODE, "NONE", 0) ;
    incrStat(RESPONSE_CODE, "OK", 0) ;
    incrStat(RESPONSE_CODE, "RC100", 0) ;
    incrStat(RESPONSE_CODE, "RC200", 0) ;
    incrStat(RESPONSE_CODE, "RC300", 0) ;
    incrStat(RESPONSE_CODE, "RC400", 0) ;
    incrStat(RESPONSE_CODE, "RC500", 0) ;
    incrStat(RESPONSE_CODE, "Unknown", 0) ;

    incrStat(ERROR_CODE, "All", 0) ;
    incrStat(ERROR_CODE, "None", 0) ;
    incrStat(ERROR_CODE, "Error Conn", 0) ;
    incrStat(ERROR_CODE, "Conn not Auth", 0) ;
    incrStat(ERROR_CODE, "Conn socket timout", 0) ;
    incrStat(ERROR_CODE, "Conn timeout", 0) ;
    incrStat(ERROR_CODE, "Conn unknown host", 0);
    incrStat(ERROR_CODE, "DB config get", 0) ;
    incrStat(ERROR_CODE, "DB config not found", 0);
    incrStat(ERROR_CODE, "Uknown",0);

    incrStat(PAGE_TYPES, "All", 0) ;
    incrStat(PAGE_TYPES, "List", 0) ;
    incrStat(PAGE_TYPES, "Detail", 0) ;
    incrStat(PAGE_TYPES, "Other", 0) ;

    incrStat(FETCH_COUNT, "All", 0) ;
    incrStat(FETCH_COUNT, "FC0", 0) ;
    incrStat(FETCH_COUNT, "FC1-5", 0) ;
    incrStat(FETCH_COUNT, "FC5-10", 0) ;
    incrStat(FETCH_COUNT, "FC10-25", 0) ;
    incrStat(FETCH_COUNT, "FC>25", 0) ;
  }
  
  static public class Statistic {
    private String category;
    private String name;
    private int    count;
    
    public Statistic(String category, String name) {
      this.category = category;
      this.name     = name;
    }
    
    public String getCategory() { return category; }
    public String getName() { return name; }
    public int getCount() { return count; }
    
    public void incr(int count) { this.count += count; }
  }
}