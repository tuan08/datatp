package net.datatp.webcrawler.master;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.storage.hdfs.SortKeyValueFile;
import net.datatp.storage.kvdb.Segment;
import net.datatp.util.URLParser;
import net.datatp.util.text.DateUtil;
import net.datatp.util.text.TabularFormater;
import net.datatp.webcrawler.site.SiteConfig;
import net.datatp.webcrawler.site.SiteContext;
import net.datatp.webcrawler.site.SiteContextManager;
import net.datatp.webcrawler.urldb.URLDatum;
import net.datatp.webcrawler.urldb.URLDatumCommitInfo;
import net.datatp.webcrawler.urldb.URLDatumDB;
import net.datatp.webcrawler.urldb.URLDatumScheduleInfo;
/**
 * $Author: Tuan Nguyen$ 
 **/
@Component
@ManagedResource(
  objectName="net.datatp.webcrawler.fetcher:name=URLFetchScheduler", 
  description="This bean is responsible to schedule the urls and commit the downloaded urls"
)
public class URLFetchScheduler {
  private static final Logger logger = LoggerFactory.getLogger(URLPreFetchScheduler.class);
  final static int MAX_HISTORY =  100 ;

  @Autowired
  private URLPreFetchScheduler  preFetchScheduler ;
  
  @Autowired
  private URLPostFetchScheduler postFetchScheduler ;

  private boolean                          exist = false;
  private ManageThread                     manageThread;
  private LinkedList<URLDatumScheduleInfo> urldatumScheduleInfos;
  private LinkedList<URLDatumCommitInfo>   urldatumCommitInfos;
  private String                           state       = "INIT";
  private boolean                          injectUrl = false;

  public URLFetchScheduler() {
    urldatumScheduleInfos = new LinkedList<URLDatumScheduleInfo>() ;
    urldatumCommitInfos = new LinkedList<URLDatumCommitInfo>() ;
  }

  @ManagedAttribute(
    description="The state of the bean, the possible value are: INIT, STARTING, STOPPING, SCHEDULING, COMMITING"
  )
  public String getState() { return this.state ; }

  @ManagedOperation(description="URLDatum Schedule info as formatted text")
  public String formatURLDatumScheduleInfosAsText() {
    String string = "The frequency of time this bean is invoked, The frequency of time this bean is invoked.";
    return string ; 
  }

  public LinkedList<URLDatumScheduleInfo> getURLDatumScheduleInfos() { 
    return this.urldatumScheduleInfos ; 
  }

  @ManagedOperation(description="URLDatum Commit info as formatted text")
  public String formatURLCommitInfosAsText() {
    String[] header = {
      "Time", "Exec Time", "Commit URL", "New URL", "URL List", "URL Detail"
    } ;
    TabularFormater formatter = new TabularFormater(header) ;
    Iterator<URLDatumCommitInfo> i = urldatumCommitInfos.iterator() ;
    while(i.hasNext()) {
      URLDatumCommitInfo sel = i.next() ;
      formatter.addRow(
        DateUtil.asCompactDateTime(sel.getTime()), sel.getExecTime(), sel.getURLCommitCount(),
        sel.getNewURLFoundCount(), sel.getNewURLTypeList(), sel.getNewURLTypeDetail()
      );
    }
    return formatter.getFormattedText() ; 
  }

  public LinkedList<URLDatumCommitInfo> getURLDatumCommitInfos() { 
    return this.urldatumCommitInfos ; 
  }

  synchronized public void start() {
    logger.info("start URLFetchScheduler!!!!!!!!!!!!!!!!!!!!!!!") ;
    if(manageThread != null && manageThread.isAlive()) {
      exist = false;
      return ;
    }
    manageThread = new ManageThread() ;
    manageThread.setName("crawler.master.fetch-manager") ;
    state = "STARTING" ;
    manageThread.start() ;
    logger.info("Create a new thread and start URLFetchScheduler!") ;
  }

  synchronized public void stop() {
    logger.info("stop URLFetchScheduler!!!!!!!!!!!!!!!!!!!!!!!") ;
    state = "STOPPING" ;
    if(manageThread == null) return ;
    if(manageThread.isAlive()) {
      exist = true ;
      logger.info("set manage thread to exist for URLFetchScheduler") ;
    }
  }

  public void run() {
    try {
      long lastUpdateDB = 0l ;
      long updatePeriod =  1 * 24 * 3600 * 1000l ;
      URLDatumCommitInfo commitInfo = null ;
      while(!exist) {
        if(injectUrl || commitInfo == null || commitInfo.getURLCommitCount() > 0) {
          state = "SCHEDULING" ;
          URLDatumScheduleInfo sheduleInfo = preFetchScheduler.schedule() ;
          urldatumScheduleInfos.addFirst(sheduleInfo) ;
          if(urldatumScheduleInfos.size() >= MAX_HISTORY) urldatumScheduleInfos.removeLast() ;
          if(injectUrl) injectUrl = false;
        }
        state = "COMMITING" ; 
        commitInfo = postFetchScheduler.process() ;
        urldatumCommitInfos.addFirst(commitInfo) ;
        if(urldatumCommitInfos.size() >=  MAX_HISTORY) urldatumCommitInfos.removeLast() ;

        //        if(lastUpdateDB + updatePeriod < System.currentTimeMillis()) {
        //        	URLDatumDB urldatumDB = postFetchScheduler.getURLDatumDB() ;
        //        	URLDatumDBUpdater updater = new URLDatumDBUpdater(postFetchScheduler.getSiteConfigManager()) ;
        //        	urldatumDB.update(updater) ;
        //        	logger.info("\n" + updater.getUpdateInfo()) ;
        //        	lastUpdateDB = System.currentTimeMillis() ;
        //        }
        Thread.sleep(1000) ;
      }
    } catch(Throwable ex) {
      logger.error("URLDatumgFetchScheduler Error", ex) ;
    }
  }

  public void injectURL() throws Exception {
    URLDatumDB urlDatumDB = postFetchScheduler.getURLDatumDB() ;
    Segment<Text, URLDatum> segment = urlDatumDB.newSegment() ;
    SortKeyValueFile<Text, URLDatum>.Writer writer = segment.getWriter() ;
    long currentTime = System.currentTimeMillis() ;
    int count = 0 ;

    SiteContextManager siteConfigManager = postFetchScheduler.getSiteConfigManager() ;
    Iterator<Map.Entry<String, SiteContext>> i = siteConfigManager.getSiteConfigContexts().entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<String, SiteContext> entry = i.next() ;
      SiteContext context = entry.getValue() ;
      SiteConfig.Status status = context.getSiteConfig().getStatus() ;
      if(status != SiteConfig.Status.Ok) continue ;
      String[] url = context.getSiteConfig().getInjectUrl() ;
      for(String selUrl : url) {
        selUrl = selUrl.trim() ;
        if(selUrl.length() == 0) continue ;
        URLParser newURLParser = new URLParser(selUrl) ;
        URLDatum datum = new URLDatum(currentTime) ;
        datum.setDeep((byte) 1) ;
        datum.setOriginalUrl(selUrl, newURLParser) ;
        datum.setPageType(URLDatum.PAGE_TYPE_LIST) ;
        writer.append(datum.getId(), datum);
        count++ ;
      }
    }
    writer.close() ;
    urlDatumDB.autoCompact() ;
    injectUrl = true;
    logger.info("inject/update " + count + " urls") ;
  }

  public class ManageThread extends Thread {
    public void run() { URLFetchScheduler.this.run() ; }
  }
}