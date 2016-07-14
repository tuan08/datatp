package net.datatp.webcrawler.master;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.datatp.webcrawler.master.model.URLCommitInfo;
import net.datatp.webcrawler.master.model.URLScheduleInfo;
import net.datatp.webcrawler.registry.WebCrawlerRegistry;
import net.datatp.webcrawler.site.SiteConfig;
import net.datatp.webcrawler.site.SiteContextManager;

@RestController
@CrossOrigin(origins = "*")
public class CrawlerRestController {
  @Autowired
  private WebCrawlerRegistry wcRegistry;
  
  @Autowired
  private SiteContextManager siteContextManager ;
  
  @RequestMapping("/ping")
  public String ping() { return "Hi!"; }
  
  @RequestMapping("/site/get-configs")
  public List<SiteConfig> siteGetSiteConfigs() throws Exception {
    return siteContextManager.getSiteConfigs();
  }
  
  @RequestMapping("/master/report/url-commit")
  public List<URLCommitInfo> masterGetURLCommitReport(@RequestParam(value="max", defaultValue="100") int max) throws Exception {
    return wcRegistry.getMasterRegistry().getURLCommitInfo(max);
  }
  
  @RequestMapping("/master/report/url-schedule")
  public List<URLScheduleInfo> masterGetURLScheduleReport(@RequestParam(value="max", defaultValue="100") int max) throws Exception {
    return wcRegistry.getMasterRegistry().getURLScheduleInfo(max);
  }
  
  
}
