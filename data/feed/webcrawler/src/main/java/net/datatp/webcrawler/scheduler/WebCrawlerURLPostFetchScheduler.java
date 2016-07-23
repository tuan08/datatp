package net.datatp.webcrawler.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.http.crawler.scheduler.URLPostFetchScheduler;
import net.datatp.http.crawler.scheduler.URLSchedulerPluginManager;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.http.crawler.urldb.URLDatumDB;
import net.datatp.http.crawler.urldb.URLDatumDBIterator;
import net.datatp.http.crawler.urldb.URLDatumDBWriter;
import net.datatp.webcrawler.site.WebCrawlerSiteContextManager;
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
  public void setSiteContextManager(WebCrawlerSiteContextManager siteContextManager) {
    this.siteContextManager = siteContextManager;
  }

  @Autowired
  public void setURLSchedulerPluginManager(URLSchedulerPluginManager manager) {
    this.schedulerPluginManager = manager;
  }
  
  @Autowired
  public void setURLDatumDB(URLDatumDB db) { this.urlDatumDB = db; }
  
  public URLDatumDBIterator createURLDatumDBIterator() throws Exception {
    return urlDatumDB.createURLDatumDBIterator();
  }
  
  public URLDatumDBWriter createURLDatumDBWriter() throws Exception {
    return urlDatumDB.createURLDatumDBWriter();
  }
  
  @JmsListener(destination = "crawler.url.fetch.commit")
  public void schedule(List<URLDatum> urls) throws InterruptedException {
    super.schedule(urls);
  }
}