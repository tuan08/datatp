package net.datatp.util.log;

import java.io.Serializable;
import java.util.Date;

import org.apache.logging.log4j.core.impl.Log4jLogEvent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Log4jRecord implements Serializable {
  @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd/MM/yyyy HH:mm:ss")
  private Date     timestamp;
  private String   host;
  private String   appName;
  private String   threadName;
  
  private String   loggerName;
  private String   level;
  private String   message;
  private String   stacktrace;

  public Log4jRecord() {
  }

  public Log4jRecord(Log4jLogEvent event) {
    timestamp = new Date(event.getTimeMillis());
    threadName = event.getThreadName() ;
    loggerName = event.getLoggerName();
    level = event.getLevel().toString();
    message = event.getMessage().getFormat();
  }

  @JsonIgnore
  public String getId() {
    return this.loggerName + "-"  + this.timestamp + "-" + message.hashCode() ;
  }
  
  public Date getTimestamp() { return timestamp; }
  public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
  public void withTimestamp(long timestamp) { this.timestamp = new Date(timestamp); }
  
  public String getHost() { return host; }
  public void setHost(String host) { this.host = host;}

  public String getAppName() { return appName;}
  public void setAppName(String appName) { this.appName = appName; }

  public String getThreadName() { return threadName; }
  public void setThreadName(String threadName) { this.threadName = threadName; }

  public String getLoggerName() { return loggerName; }
  public void setLoggerName(String loggerName) { this.loggerName = loggerName; }

  public String getLevel() { return level; }
  public void setLevel(String level) { this.level = level; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public String getStacktrace() { return stacktrace; }
  public void setStacktrace(String stacktrace) { this.stacktrace = stacktrace; }
}
