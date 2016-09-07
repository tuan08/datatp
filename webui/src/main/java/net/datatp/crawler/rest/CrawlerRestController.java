package net.datatp.crawler.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.datatp.crawler.CrawlerApi;
import net.datatp.crawler.CrawlerStatus;
import net.datatp.crawler.fetcher.FetcherReport;
import net.datatp.crawler.scheduler.metric.URLCommitMetric;
import net.datatp.crawler.scheduler.metric.URLScheduleMetric;
import net.datatp.crawler.site.SiteConfig;
import net.datatp.crawler.site.analysis.SiteStructureAnalyzer;
import net.datatp.crawler.site.analysis.SiteStructureAnalyzerConfig;
import net.datatp.crawler.site.analysis.SiteStructureAnalyzerService;
import net.datatp.crawler.site.analysis.URLData;
import net.datatp.crawler.site.analysis.URLSiteStructure;
import net.datatp.util.URLInfo;
import net.datatp.util.dataformat.DataSerializer;
import net.datatp.util.io.IOUtil;
import com.fasterxml.jackson.core.type.TypeReference;

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
  
  //@RequestMapping(value = "/crawler/site/save", method = RequestMethod.POST)
  @PostMapping("/crawler/site/save")
  public SiteConfig siteSave(@RequestBody SiteConfig config) throws Exception {
    System.out.println(DataSerializer.JSON.toString(config));
    crawlerApi.siteSave(config);
    return config;
  }
  
  @RequestMapping(value = "/crawler/site/export")
  public String siteExport(HttpServletResponse response) throws Exception {
    response.setContentType("application/json");
    response.setHeader("Content-Disposition", "attachment;filename=site-config.json");
    return DataSerializer.JSON.toString(crawlerApi.siteGetSiteConfigs());
  }
  
  @RequestMapping(value = "/crawler/site/import", method = RequestMethod.POST)
  public boolean siteImport(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws Exception {
    String fileName = file.getOriginalFilename();
    System.out.println("receive upload " + fileName);
    byte[] data = IOUtil.getStreamContentAsBytes(file.getInputStream());
    List<SiteConfig> siteConfigs = DataSerializer.JSON.fromBytes(data,  new TypeReference<List<SiteConfig>>() {});
    SiteConfig[] array = siteConfigs.toArray(new SiteConfig[siteConfigs.size()]);
    crawlerApi.siteSave(array);
    return true;
  }
  
  @RequestMapping(value = "/crawler/site/upload", method = RequestMethod.POST)
  public String upload(MultipartHttpServletRequest request) throws Exception {
    System.out.println("content type = " + request.getContentType());
    System.out.println("content length = " + request.getContentLength());
    System.out.println("file map = " + request.getFileMap());
    Iterator<String> itrator = request.getFileNames();
    MultipartFile multiFile = request.getFile(itrator.next());
    try {
      // just to show that we have actually received the file
      System.out.println("File Length:" + multiFile.getBytes().length);
      System.out.println("File Type:" + multiFile.getContentType());
      String fileName=multiFile.getOriginalFilename();
      System.out.println("File Name:" +fileName);
      String path=request.getServletContext().getRealPath("/");
      //making directories for our required path.
      byte[] bytes = multiFile.getBytes();
      System.out.println(new String(bytes));
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("Error while loading the file");
    }
    return "{ message: 'File Uploaded successfully.'}";
  }

  
  @RequestMapping("/crawler/scheduler/report/url-commit")
  public List<URLCommitMetric> schedulerGetURLCommitReport(@RequestParam(value="max", defaultValue="100") int max) throws Exception {
    return crawlerApi.schedulerGetURLCommitReport(max);
  }
  
  @RequestMapping("/crawler/scheduler/report/url-schedule")
  public List<URLScheduleMetric> schedulerGetURLScheduleReport(@RequestParam(value="max", defaultValue="100") int max) throws Exception {
    return crawlerApi.schedulerGetURLScheduleReport(max);
  }
  
  @RequestMapping("/crawler/fetcher/report")
  public FetcherReport fetcherGetReport(@RequestParam(value="id") String id) throws Exception {
    return crawlerApi.getFetcherReport(id);
  }
  
  @RequestMapping("/crawler/status")
  public CrawlerStatus getStatus() throws Exception {
    return crawlerApi.getCrawlerStatus();
  }
  
  @RequestMapping("/crawler/start")
  public CrawlerStatus crawlerStart() throws Exception {
    crawlerApi.crawlerStart();
    return crawlerApi.getCrawlerStatus();
  }
  
  @RequestMapping("/crawler/stop")
  public CrawlerStatus crawlerStop() throws Exception {
    crawlerApi.crawlerStop();
    return crawlerApi.getCrawlerStatus();
  }
}