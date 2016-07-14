package net.datatp.webcrawler.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.datatp.webcrawler.master.model.URLCommitInfo;
import net.datatp.webcrawler.master.model.URLScheduleInfo;
import net.datatp.webcrawler.registry.WebCrawlerRegistry;

@Component
public class CrawlerMasterReporter {
  @Autowired
  private WebCrawlerRegistry wcRegistry;
  
  public void report(URLScheduleInfo info) throws Exception {
    if(info == null) return;
    wcRegistry.getMasterRegistry().addReportURLScheduleInfo(info);
  }
  
  public void report(URLCommitInfo info) throws Exception {
    if(info == null) return;
    wcRegistry.getMasterRegistry().addReport(info);
  }
  
}
