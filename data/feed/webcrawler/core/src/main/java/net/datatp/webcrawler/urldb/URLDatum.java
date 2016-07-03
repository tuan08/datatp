package net.datatp.webcrawler.urldb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Date;

import org.apache.hadoop.io.Text;

import net.datatp.storage.kvdb.Record;
import net.datatp.util.URLParser;
import net.datatp.util.text.DateUtil;
import net.datatp.util.text.StringUtil;
import net.datatp.webcrawler.ErrorCode;
import net.datatp.webcrawler.ResponseCode;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 13, 2010  
 */
public class URLDatum implements Record, Serializable  {
  final static public byte STATUS_NEW            = 0 ;
  final static public byte STATUS_WAITING        = 1 ;
  final static public byte STATUS_FETCHING       = 2 ;

  final static public byte PAGE_TYPE_UNKNOWN     = 0 ;
  final static public byte PAGE_TYPE_LIST        = 1 ;
  final static public byte PAGE_TYPE_DETAIL      = 2 ;

  final static public Text DEFAULT_CONTENT_TYPE = new Text("unknow/unknow") ;
  final static public Text EMPTY_TEXT  = new Text("".getBytes(StringUtil.UTF8));

  private Text  id ;
  private long  createdTime ;
  private Text  url ;
  private Text  redirectUrl = EMPTY_TEXT ;

  private Text  anchorText = EMPTY_TEXT;
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
  private byte  deep ;
  private byte  status = STATUS_NEW ;
  private Text  contentType = DEFAULT_CONTENT_TYPE;

  public URLDatum() {
  }

  public URLDatum(long time) {
    this.createdTime = time ;
  }

  public Text getId() { return this.id ; }
  public void setId(Text id) { this.id = id ; }

  public long getCreatedTime() { return this.createdTime ; }

  public String getOriginalUrlAsString() { 
    if(url == null) return null ;
    return url.toString(); 
  }

  public void setOrginalUrl(String url) { 
    setOriginalUrl(url, new URLParser(url)) ;
  }

  public void setOriginalUrl(String url, URLParser urlNorm) { 
    this.id = new Text(urlNorm.getHostMD5Id()) ;
    this.url = new Text(url.getBytes(StringUtil.UTF8)); 
  }

  public Text getOriginalUrl() { return this.url ; }

  public Text getRedirectUrl() { return this.redirectUrl ; }
  public void setRedirectUrl(Text text) { this.redirectUrl = text ; }
  public void setRedirectUrl(String url) { this.redirectUrl = new Text(url.getBytes(StringUtil.UTF8)); }

  public String getFetchUrl() {
    if(this.redirectUrl.getLength() > 0) return this.redirectUrl.toString() ;
    return url.toString() ;
  }

  public Text getAnchorText() { return this.anchorText ; }
  public String getAnchorTextAsString() { 
    if(anchorText == null) return "" ;
    return anchorText.toString(); 
  }
  public void setAnchorText(Text text) { this.anchorText = text ; }
  public void setAnchorText(String text) { 
    this.anchorText = new Text(text.getBytes(StringUtil.UTF8)); 
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

  public Text getContentType() { return this.contentType ; }
  public void setContentType(String type) { this.contentType = new Text(type) ; }

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
    this.id  = new Text() ;
    this.id.readFields(in) ;
    this.createdTime = in.readLong() ;
    this.url = new Text()  ;
    this.url.readFields(in) ;
    this.redirectUrl = new Text() ;
    this.redirectUrl.readFields(in) ;
    this.anchorText = new Text()  ;
    this.anchorText.readFields(in) ;

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
    this.contentType = new Text() ;
    this.contentType.readFields(in) ;
  }

  public void write(DataOutput out) throws IOException {
    this.id.write(out) ;
    out.writeLong(this.createdTime) ;
    this.url.write(out) ;
    this.redirectUrl.write(out) ;
    this.anchorText.write(out) ;
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
    this.contentType.write(out) ;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    write(out) ;
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    readFields(in) ;
  }

  public void dump(PrintStream out) {
    out.println(toString()) ;
  }

  public String encodeString() {
    StringBuilder b = new StringBuilder() ;
    b.append("url=").append(this.url).append(" | ") ;
    b.append("next-fetch-time=").append(DateUtil.asCompactDateTime(nextFetchTime)).append(" | ") ;
    b.append("error-counter=").append(errorCount).append(" | ") ;
    b.append("last-response-code=").append(lastResponseCode).append(" | ") ;
    b.append("last-error-code=").append(lastErroCode).append(" | ") ;
    b.append("last-fetch-schedule-at=").append(this.lastFetchScheduleAt).append(" | ") ;
    b.append("last-fetch-finish-at").append(this.lastFetchFinishAt).append(" | ") ;
    b.append("last-fetch-download-time=").append(lastFetchDownloadTime).append(" | ") ;
    b.append("deep=").append(deep).append(" | ") ;
    b.append("status=").append(status) ;
    return b.toString() ;
  }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("URL: ").append(this.url).append("\n") ;
    b.append("Next Fetch Time: ").append(new Date(nextFetchTime)).append("\n") ;
    b.append("Error Counter: ").append(errorCount).append("\n") ;
    b.append("Last Response Code: ").append(lastResponseCode).append("\n") ;
    b.append("Last Error Type: ").append(lastErroCode).append("\n") ;
    b.append("Last Fetch Schedule At: ").append(this.lastFetchScheduleAt).append("\n") ;
    b.append("Last Fetch Finish At: ").append(this.lastFetchFinishAt).append("\n") ;
    b.append("Last Download Time: ").append(lastFetchDownloadTime).append("\n") ;
    b.append("Deep: ").append(deep).append("\n") ;
    b.append("Status: ").append(status).append("\n") ;
    return b.toString() ;
  }

  public byte[] toBytes() throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
    DataOutputStream out = new DataOutputStream(bos) ;
    write(out) ;
    out.close() ;
    return bos.toByteArray() ;
  }

  public void fromBytes(byte[] data) throws IOException {
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(data)) ;
    readFields(in) ;
    in.close() ;
  }
}