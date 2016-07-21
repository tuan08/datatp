package net.datatp.webcrawler.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.http.crawler.scheduler.URLPostFetchScheduler;
import net.datatp.http.crawler.scheduler.URLSchedulerPluginManager;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDBIterator;
import net.datatp.http.crawler.urldb.URLDatumDBWriter;
import net.datatp.webcrawler.site.WebCrawlerSiteContextManager;
import net.datatp.webcrawler.urldb.URLDatumRecordDB;
import net.datatp.webcrawler.urldb.URLDatumRecordDBIterator;
import net.datatp.webcrawler.urldb.URLDatumRecordDBWriter;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 29, 2010  
 */
@Component
@ManagedResource(
  objectName="org.headvances.crawler.fetch:name=WebCrawlerURLPostFetchScheduler", 
  description="This bean is responsible to commit the downloaded urls"
)
public class WebCrawlerURLPostFetchScheduler extends URLPostFetchScheduler {
  @Autowired
  private URLDatumRecordDB urlDatumDB ;
  
  @Autowired
  public void setSiteContextManager(WebCrawlerSiteContextManager siteContextManager) {
    this.siteContextManager = siteContextManager;
  }

  @Autowired
  public void setURLSchedulerPluginManager(URLSchedulerPluginManager manager) {
    this.schedulerPluginManager = manager;
  }
  
  public URLDatumDBIterator createURLDatumDBIterator() throws Exception {
    return new URLDatumRecordDBIterator(urlDatumDB);
  }
  
  public URLDatumDBWriter createURLDatumDBWriter() throws Exception {
    return new URLDatumRecordDBWriter(urlDatumDB);
  }
  
  @JmsListener(destination = "crawler.url.fetch.commit")
  public void schedule(List<URLDatum> urls) throws InterruptedException {
    for(int i = 0; i < urls.size(); i++) {
      URLDatum datum = urls.get(i) ;
      if(datum.getStatus() == URLDatum.STATUS_NEW) {
        URLDatumEntry entry = newURLDatums.get(datum.getId()) ;
        if(entry == null) {
          newURLDatums.put(datum.getId(), new URLDatumEntry(datum)) ;
        } else {
          entry.hit++ ;
        }
      } else {
        commitURLDatumQueue.put(datum) ;
      }
    }
  }
}