package net.datatp.crawler.rest;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.datatp.crawler.CrawlerApi;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.analysis.SiteStructureAnalyzer;
import net.datatp.crawler.site.analysis.SiteStructureAnalyzerConfig;
import net.datatp.crawler.site.analysis.SiteStructureAnalyzerService;
import net.datatp.crawler.site.analysis.URLSiteStructure;
import net.datatp.crawler.site.analysis.URLData;
import net.datatp.util.URLInfo;
import net.datatp.util.dataformat.DataSerializer;

@RestController
@CrossOrigin(origins = "*")
public class CrawlerRestController {
  @Autowired
  private CrawlerApi crawlerApi;
  
  private SiteStructureAnalyzerService siteStructureAnalyzerService;
  
  @PostConstruct
  public void onInit() {
    siteStructureAnalyzerService = new SiteStructureAnalyzerService(30 * 60 * 1000);
  }
  
  @PreDestroy
  public void onDestroy() {
    siteStructureAnalyzerService.onDestroy();
  }
  
  @RequestMapping("/crawler/site/configs")
  public List<SiteConfig> siteGetSiteConfigs() throws Exception {
    return crawlerApi.siteGetSiteConfigs();
  }  
  
  @RequestMapping(value = "/crawler/site/analyzed-site-url", method = RequestMethod.POST)
  public URLSiteStructure siteGetAnalyzedSiteUrl(@RequestBody SiteStructureAnalyzerConfig config) throws Exception {
    SiteStructureAnalyzer analyzer = siteStructureAnalyzerService.getSiteStructureAnalyzer(config);
    return analyzer.getSiteStructure().getUrlSiteStructure();
  }
  
  @RequestMapping(value = "/crawler/site/reanalyse-site-url", method = RequestMethod.POST)
  public URLSiteStructure siteReanalyseSiteUrl(@RequestBody SiteStructureAnalyzerConfig config) throws Exception {
    SiteStructureAnalyzer analyzer = siteStructureAnalyzerService.reanalyse(config);
    return analyzer.getSiteStructure().getUrlSiteStructure();
  }
  
  @RequestMapping(value = "/crawler/site/recrawl-site-url", method = RequestMethod.POST)
  public URLSiteStructure siteRecrawlSiteUrl(@RequestBody SiteStructureAnalyzerConfig config) throws Exception {
    config.setForceNew(true);
    SiteStructureAnalyzer analyzer = siteStructureAnalyzerService.getSiteStructureAnalyzer(config);
    return analyzer.getSiteStructure().getUrlSiteStructure();
  }
  
  @RequestMapping(value = "/crawler/site/analyzed-url-data")
  public URLData siteGetAnalyzedURLData(@RequestParam("url") String url) throws Exception {
    URLInfo urlAnalyzer = new URLInfo(url);
    SiteStructureAnalyzer analyzer = siteStructureAnalyzerService.getSiteStructureAnalyzer(urlAnalyzer.getHost());
    if(analyzer != null) return analyzer.getSiteStructure().getURLData(url);
    return new URLData(new URLInfo(url), "No Data");
  }
  
  @RequestMapping(value = "/crawler/site/save", method = RequestMethod.POST)
  public SiteConfig siteSave(@RequestBody SiteConfig config) throws Exception {
    System.out.println(DataSerializer.JSON.toString(config));
    crawlerApi.siteSave(config);
    return config;
  }
  
  @RequestMapping("/crawler/scheduler/report/url-commit")
  public List<URLCommitMetric> schedulerGetURLCommitReport(@RequestParam(value="max", defaultValue="100") int max) throws Exception {
    return crawlerApi.schedulerGetURLCommitReport(max);
  }
  
  @RequestMapping("/crawler/scheduler/report/url-schedule")
  public List<URLScheduleMetric> schedulerGetURLScheduleReport(@RequestParam(value="max", defaultValue="100") int max) throws Exception {
    return crawlerApi.schedulerGetURLScheduleReport(max);
  }
  
  
}