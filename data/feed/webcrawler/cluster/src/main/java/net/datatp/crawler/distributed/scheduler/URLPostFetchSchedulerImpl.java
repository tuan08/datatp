package net.datatp.crawler.distributed.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import net.datatp.crawler.scheduler.URLPostFetchScheduler;
import net.datatp.crawler.scheduler.URLSchedulerPluginManager;
import net.datatp.crawler.site.SiteContextManager;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumDB;
import net.datatp.crawler.urldb.URLDatumDBIterator;
import net.datatp.crawler.urldb.URLDatumDBWriter;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Apr 29, 2010  
 */
@Component
@ManagedResource(
  objectName="org.headvances.crawler.fetch:name=URLPostFetchSchedulerImpl", 
  description="This bean is responsible to commit the downloaded urls"
)
public class URLPostFetchSchedulerImpl extends URLPostFetchScheduler {
  @Autowired
  public void setSiteContextManager(SiteContextManager siteContextManager) {
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