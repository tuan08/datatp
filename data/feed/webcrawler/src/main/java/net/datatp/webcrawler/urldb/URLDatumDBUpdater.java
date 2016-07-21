package net.datatp.webcrawler.urldb;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import net.datatp.http.ResponseCode;
import net.datatp.http.crawler.site.SiteContextManager;
import net.datatp.http.crawler.site.URLContext;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.storage.kvdb.RecordUpdater;
import net.datatp.util.URLNormalizerProcessor;
import net.datatp.xhtml.util.URLSessionIdCleaner;

public class URLDatumDBUpdater implements RecordUpdater<URLDatumRecord> {
  static URLNormalizerProcessor[] URL_PROCESSORS = { new URLSessionIdCleaner()} ;

  private long currentTime ;
  private SiteContextManager manager ;
  private int noConfigCount ;
  private int ignorePageCount ;
  private int ignoreDomainCount ;
  private int expirePageListCount ;
  private int expirePageDetailCount ;
  private int rc4xxCount ;
  private int rc3xxCount ;
  private int moreThan3ErrorCount ;
  private int count ;

  public URLDatumDBUpdater(SiteContextManager manager) {
    this.manager = manager ;
    this.currentTime = System.currentTimeMillis() ;
  }

  public int getNoConfigCount() { return noConfigCount ; }

  public int getIgnorePageCount() { return this.ignorePageCount ; }

  public int getIgnoreDomainCount() { return this.ignoreDomainCount ; }

  public int getExpirePageListCount() { return this.expirePageListCount ; }

  public int getExpirePageDetailCount() { return this.expirePageDetailCount ; }

  public int getRC4xxCount() { return this.rc4xxCount  ; }

  public int getRC3xxCount() { return this.rc3xxCount ; }

  public int getCount() { return this.count ; }

  public int getDeleteCount() { 
    return noConfigCount  + ignorePageCount + expirePageListCount + 
        expirePageDetailCount + ignoreDomainCount + rc4xxCount + rc3xxCount + moreThan3ErrorCount;
  }

  public String getUpdateInfo() {
    StringBuilder b = new StringBuilder() ;
    b.append("Update URLDatumRecordDB, total " + count + " records \n") ;
    b.append("Update URLDatumRecordDB, delete  "  + getDeleteCount() + " records\n") ;
    b.append("Update URLDatumRecordDB, no config " + getNoConfigCount() + " records\n") ;
    b.append("Update URLDatumRecordDB, ignore domain " + getIgnoreDomainCount() + " records\n") ;
    b.append("Update URLDatumRecordDB, ignore page " + getIgnorePageCount() + " records\n") ;
    b.append("Update URLDatumRecordDB, expire page list " + getExpirePageListCount() + " records\n") ;
    b.append("Update URLDatumRecordDB, expire page detail " + getExpirePageDetailCount() + " records\n") ;
    b.append("Update URLDatumRecordDB, rc 300 " + getRC3xxCount() + " records") ;
    b.append("Update URLDatumRecordDB, rc 400 " + getRC4xxCount() + " records") ;
    b.append("Update URLDatumRecordDB, > 3 errors " + this.moreThan3ErrorCount + " records") ;
    return b.toString() ;
  }

  public URLDatumRecord update(Writable key, URLDatumRecord datum) {
    count++ ;

    if(datum.getErrorCount() >= 3) {
      moreThan3ErrorCount++ ;
      return null ;
    }
    Text textKey = ((Text) key) ;
    String hostname = textKey.toString() ;
    if(hostname.startsWith(".") || hostname.startsWith("www.")) {
      ignoreDomainCount++ ;
      return null ;
    }
    URLContext context = manager.getURLContext(datum.getOriginalUrlAsString()) ;
    datum = updateBySiteConfig(context, datum) ;
    if(datum == null) return null ;

    if(datum.getDeep() == 1) return datum ;

    byte pageType = datum.getPageType() ;
    if(pageType == URLDatum.PAGE_TYPE_LIST) {
      datum = updatePageList(datum) ;
    } else if(pageType == URLDatum.PAGE_TYPE_DETAIL) {
      datum = updatePageDetail(datum) ;
    }
    return datum;
  } 

  private URLDatumRecord updateBySiteConfig(URLContext context, URLDatumRecord datum) {
    //SiteConfig database is no longer maintain this url configuration, delete the record
    if(context == null) {
      noConfigCount++ ;
      return null ;
    }
    return datum ;
  }

  private URLDatumRecord updatePageList(URLDatumRecord datum) {
    //keep the list page in the database for only 7 days
    final long keep7days = 7 * 24 * 3600 * 1000l ;
    if(datum.getCreatedTime() + keep7days < currentTime) {
      expirePageListCount++ ;
      return null ;
    }
    return datum ;
  }

  private URLDatumRecord updatePageDetail(URLDatumRecord datum) {
    //Client error 4xx
    if(ResponseCode.isIn4XXGroup(datum.getLastResponseCode())) {
      rc4xxCount++ ;
      return null ;
    }
    //Redirection 3xx
    if(ResponseCode.isIn3XXGroup(datum.getLastResponseCode())) {
      rc3xxCount++ ;
      return null ;
    }

    //keep the list page in the database for only 45 days
    final long keep45days = 45 * 24 * 3600 * 1000l ;
    if(datum.getCreatedTime() + keep45days < currentTime) {
      expirePageDetailCount++ ;
      return null ;
    }
    return datum ;
  }
}