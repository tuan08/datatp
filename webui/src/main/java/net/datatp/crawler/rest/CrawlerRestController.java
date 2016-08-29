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
import net.datatp.crawler.site.analysis.URLStructure;
import net.datatp.util.URLAnalyzer;
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
    return analyzer.getSiteStructure().getUrlStructure();
  }
  
  @RequestMapping(value = "/crawler/site/analyzed-url")
  public URLStructure siteGetAnalyzedUrl(@RequestParam("url") String url) throws Exception {
    URLAnalyzer urlAnalyzer = new URLAnalyzer(url);
    SiteStructureAnalyzer analyzer = siteStructureAnalyzerService.getSiteStructureAnalyzer(urlAnalyzer.getHost());
    if(analyzer != null) return analyzer.getSiteStructure().getURLStructure(url);
    return new URLStructure(new URLAnalyzer(url), "No Data");
  }
  
//  @RequestMapping(value = "/crawler/site/analyzed-url-xhtml")
//  public String siteGetAnalyzedUrlContent(@RequestParam("url") String url) throws Exception {
//    System.out.println("url = " + url);
//    URLAnalyzer urlAnalyzer = new URLAnalyzer(url);
//    SiteStructureAnalyzer analyzer = siteStructureAnalyzerService.getSiteStructureAnalyzer(urlAnalyzer.getHost());
//    if(analyzer != null) return analyzer.getSiteStructure().getURLStructure(url).getXhtml();
//    return "No Data";
//  }
//  
  
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