package net.datatp.crawler.urldb;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import net.datatp.crawler.http.ErrorCode;
import net.datatp.crawler.http.ResponseCode;
import net.datatp.util.URLInfo;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 13, 2010  
 */
public class URLDatum implements  Externalizable {
  final static public byte STATUS_NEW            = 0 ;
  final static public byte STATUS_WAITING        = 1 ;
  final static public byte STATUS_FETCHING       = 2 ;

  final static public byte PAGE_TYPE_UNKNOWN     = 0 ;
  final static public byte PAGE_TYPE_LIST        = 1 ;
  final static public byte PAGE_TYPE_DETAIL      = 2 ;

  final static public String DEFAULT_CONTENT_TYPE = "unknow/unknow" ;
  //final static public Text EMPTY_TEXT  = new Text("".getBytes(StringUtil.UTF8));

  private String  id ;
  private long  createdTime ;
  private String  url ;
  private String  redirectUrl ;

  private String  anchorText ;
  private long  nextFetchTime ;

  private int   fetchCount ;
  private int   errorCount ;
  private short lastResponseCode = ResponseCode.NONE ;
  private byte  lastErroCode     = ErrorCode.ERROR_TYPE_NONE ;
  private long  lastFetchWaitingPeriod ;
  private long  lastFetchScheduleAt ;
  private long  lastFetchFinishAt ;
  private long  lastFetchDownloadTime ;
  private int   lastDownloadDataSize  ;
  private byte  pageType ;
  private byte  deep;
  private byte  status      = STATUS_NEW ;
  private String  contentType = DEFAULT_CONTENT_TYPE;

  public URLDatum() {
  }

  public URLDatum(long time) {
    this.createdTime = time ;
  }

  public String getId() { return this.id ; }
  public void setId(String id) { this.id = id ; }

  public long getCreatedTime() { return this.createdTime ; }
  
  public String getOriginalUrl() { return this.url ; }
  
  public void setOrginalUrl(String url) { 
    setOriginalUrl(url, new URLInfo(url)) ;
  }

  public void setOriginalUrl(String url, URLInfo urlParser) { 
    this.id = urlParser.getHostMD5Id() ;
    this.url = url; 
  }

  public String getRedirectUrl() { return this.redirectUrl ; }
  public void setRedirectUrl(String url) { this.redirectUrl = url; }

  public String fetchUrl() {
    if(redirectUrl != null) return redirectUrl ;
    return url ;
  }

  public String getAnchorText() { return anchorText ; }
  public String getAnchorTextAsString() { 
    if(anchorText == null) return "" ;
    return anchorText; 
  }
  public void setAnchorText(String text) { 
    this.anchorText = text; 
  }

  public long getNextFetchTime() { return this.nextFetchTime; }
  public void setNextFetchTime(long nextFetchTime) { this.nextFetchTime = nextFetchTime;}

  public int  getFetchCount() { return fetchCount; }
  public void setFetchCount(int count) { this.fetchCount = count; }

  public int  getErrorCount() { return errorCount; }
  public void setErrorCount(int errorCounter) { this.errorCount = errorCounter; }

  public short getLastResponseCode() { return lastResponseCode; }
  public void  setLastResponseCode(short code) {
    this.lastResponseCode = code;
    if(code != 200) {
      errorCount++ ;
    } else {
      errorCount  = 0 ;
    }
  }

  public byte getLastErrorCode() { return this.lastErroCode ; }
  public void setLastErrorCode(byte type) { this.lastErroCode = type ; }

  public long getLastFetchWaitingPeriod() { return this.lastFetchWaitingPeriod ; }
  public void setLastFetchWaitingPeriod(long value) { this.lastFetchWaitingPeriod = value ; }

  public long getLastFetchScheduleAt() { return this.lastFetchScheduleAt ; }
  public void setLastFetchScheduleAt(long at) { this.lastFetchScheduleAt = at ; }

  public long getLastFetchFinishAt() { return this.lastFetchFinishAt ; }
  public void setLastFetchFinishAt(long at) { this.lastFetchFinishAt = at ; }

  public long getLastFetchDownloadTime() { return this.lastFetchDownloadTime ; }
  public void setLastFetchDownloadTime(long time) { this.lastFetchDownloadTime = time ; }

  public int getLastDownloadDataSize() { return this.lastDownloadDataSize ; }
  public void setLastDownloadDataSize(int value) { this.lastDownloadDataSize = value ; }

  public byte getPageType() { return this.pageType ; }
  public void setPageType(byte value) { this.pageType = value ; }

  public byte  getDeep() { return deep ;}
  public void setDeep(byte deep) { this.deep = deep; }

  public byte  getStatus() { return this.status  ; }
  public String getStatusAsString() {
    //REVIEW: return a string according to the status 
    return Byte.toString(this.status);
  }
  public void setStatus(byte status) { this.status = status ;}

  public String getContentType() { return this.contentType ; }
  public void setContentType(String type) { this.contentType = type ; }

  public void copy(URLDatum other) {
    this.id = other.id ;
    this.createdTime = other.createdTime ;
    this.url = other.url  ;
    this.redirectUrl = other.redirectUrl ;
    this.nextFetchTime = other.nextFetchTime ;
    this.fetchCount = other.fetchCount ;
    this.errorCount = other.errorCount ;
    this.lastResponseCode = other.lastResponseCode ;
    this.lastErroCode = other.lastErroCode ;
    this.lastFetchWaitingPeriod = other.lastFetchWaitingPeriod ;
    this.lastFetchScheduleAt = other.lastFetchScheduleAt ;
    this.lastFetchFinishAt = other.lastFetchFinishAt ;
    this.lastFetchDownloadTime = other.lastFetchDownloadTime ;
    this.lastDownloadDataSize = other.lastDownloadDataSize ;
    this.pageType = other.pageType ;
    this.deep = other.deep ;
    this.status = other.status ;
    this.contentType = other.contentType ;
  }

  public void readFields(DataInput in) throws IOException {
    this.id  = readString(in) ;
    this.createdTime = in.readLong() ;
    this.url = readString(in) ;
    this.redirectUrl = readString(in) ;
    this.anchorText = readString(in) ;

    this.nextFetchTime = in.readLong() ;
    this.fetchCount = in.readInt() ;
    this.errorCount = in.readInt() ;
    this.lastResponseCode = in.readShort() ;
    this.lastErroCode = in.readByte() ;
    this.lastFetchWaitingPeriod = in.readLong() ;
    this.lastFetchScheduleAt = in.readLong() ;
    this.lastFetchFinishAt = in.readLong() ;
    this.lastFetchDownloadTime = in.readLong() ;
    this.lastDownloadDataSize = in.readInt() ;

    this.pageType = in.readByte() ;
    this.deep = in.readByte() ;
    this.status = in.readByte() ;
    this.contentType = readString(in) ;
  }

  public void write(DataOutput out) throws IOException {
    writeString(out, id) ;
    out.writeLong(this.createdTime) ;
    writeString(out, url);
    writeString(out, redirectUrl) ;
    writeString(out, anchorText) ;
    out.writeLong(nextFetchTime) ;
    out.writeInt(fetchCount) ;
    out.writeInt(errorCount) ;
    out.writeShort(lastResponseCode) ;
    out.writeByte(lastErroCode) ;
    out.writeLong(lastFetchWaitingPeriod) ;
    out.writeLong(lastFetchScheduleAt) ;
    out.writeLong(lastFetchFinishAt) ;
    out.writeLong(lastFetchDownloadTime) ;
    out.writeInt(lastDownloadDataSize) ;

    out.writeByte(pageType) ;
    out.writeByte(deep) ;
    out.writeByte(status) ;
    writeString(out, contentType) ;
  }
  
  void writeString(DataOutput out, String string) throws IOException {
    if(string == null) {
      out.writeBoolean(false);
    } else {
      out.writeBoolean(true);
      out.writeUTF(string);
    }
  }
  
  String readString(DataInput in) throws IOException {
    boolean present = in.readBoolean();
    if(!present) return null;
    return in.readUTF();
  }
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    write(out) ;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    readFields(in) ;
  }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("URL: ").append(this.url).append("\n") ;
    b.append("Next Fetch Time: ").append(new Date(nextFetchTime)).append("\n") ;
    b.append("Error Counter: ").append(errorCount).append("\n") ;
    b.append("Last Response Code: ").append(lastResponseCode).append("\n") ;
    b.append("Last Error WebPageType: ").append(lastErroCode).append("\n") ;
    b.append("Last Fetch Schedule At: ").append(this.lastFetchScheduleAt).append("\n") ;
    b.append("Last Fetch Finish At: ").append(this.lastFetchFinishAt).append("\n") ;
    b.append("Last Download Time: ").append(lastFetchDownloadTime).append("\n") ;
    b.append("Deep: ").append(deep).append("\n") ;
    b.append("Status: ").append(status).append("\n") ;
    return b.toString() ;
  }
}