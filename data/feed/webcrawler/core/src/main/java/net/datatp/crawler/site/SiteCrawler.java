package net.datatp.crawler.site;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.util.URLParser;
import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.WDataExtractContext;
import net.datatp.xhtml.util.URLRewriter;
import net.datatp.xhtml.util.URLSessionIdCleaner;
import net.datatp.xhtml.util.WDataHttpFetcher;
import net.datatp.xhtml.xpath.XPath;
import net.datatp.xhtml.xpath.XPathStructure;

public class SiteCrawler {
  final static Logger logger  = LoggerFactory.getLogger(SiteCrawler.class);
  
  private int              maxDownload;
  private SiteContext      siteContext;
  private WDataHttpFetcher fetcher;
  private Set<String>      visistedUrls = new HashSet<>();

  public SiteCrawler(String site, String injectUrl, int maxDownload) {
    this(new SiteConfig("default", site, injectUrl, 3), maxDownload);
  }
  
  public SiteCrawler(SiteConfig siteConfig, int maxDownload) {
    siteConfig.setCrawlSubDomain(false);
    this.siteContext = new SiteContext(siteConfig, null);
    this.maxDownload = maxDownload;
    this.fetcher     = new WDataHttpFetcher();
  }
  
  public SiteContext getSiteContext() { return this.siteContext; }
  
  public void crawl() {
    int downloadCount = 0;
    URL[] urls = { new URL("", siteContext.getSiteConfig().getInjectUrl()[0], 1) };
    while(urls.length > 0 && downloadCount < maxDownload) {
      URLExtractor urlExtractor = new URLExtractor(siteContext);
      for(URL selUrl : urls) {
        if(visistedUrls.contains(selUrl.url)) continue;
        try {
          WData wdata = fetcher.fetch(selUrl.anchorText, selUrl.url);
          WDataExtractContext wdataExtractCtx = new WDataExtractContext(wdata);
          visistedUrls.add(selUrl.getUrl());
          urlExtractor.extract(selUrl, wdataExtractCtx);
          onWData(wdataExtractCtx);
          downloadCount++;
        } catch(InterruptedException ex) {
          return;
        } catch(Exception ex) {
          logger.error("Cannot process url " + selUrl.getUrl(), ex);
        }
        if(downloadCount == maxDownload) break;
      }
      urls = urlExtractor.getUrls();
    }
  }
  
  public void onWData(WDataExtractContext ctx) {
    System.out.println("Fetch: " + ctx.getWdata().getUrl());
  }
  
  static public class URL {
    private String anchorText;
    private String url;
    private int    deep;
    
    public URL(String anchorText, String url, int deep) {
      this.anchorText = anchorText;
      this.url        = url;
      this.deep       = deep;
    }

    public String getAnchorText() { return anchorText; }
    public void setAnchorText(String anchorText) { this.anchorText = anchorText; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public int getDeep() { return deep; }
    public void setDeep(int deep) { this.deep = deep; }
  }
  
  static public class URLExtractor {
    static String[] EXCLUDE_EXT_PATTERNS = {
      ".*\\.(pdf|doc|xls|ppt)",
      ".*\\.(rss|rdf)",
      ".*\\.(img|jpg|jpeg|gif|png)",
      ".*\\.(exe)",
      ".*\\.(zip|arj|rar|lzh|z|gz|gzip|tar|bin|rar)" ,
      ".*\\.(mp3|m4a|wav|ra|ram|aac|aif|avi|mpg|mpeg|qt|plj|asf|mov|rm|mp4|wma|wmv|mpe|mpa)",
      ".*\\.(r0*|r1*|a0*|a1*|tif|tiff|msi|msu|ace|iso|ogg|7z|sea|sit|sitx|pps|bz2|xsl)"
    };
    static private URLSessionIdCleaner URL_CLEANER = new URLSessionIdCleaner();
    
    private SiteContext         siteContext;
    private URLRewriter         urlRewriter = new URLRewriter();
    private Map<String, URL>    urls        = new HashMap<>();
    private Pattern[]           excludePatternMatchers = {};
   
    public URLExtractor(SiteContext ctx) {
      this.siteContext = ctx;
      excludePatternMatchers = new Pattern[EXCLUDE_EXT_PATTERNS.length];
      for (int i = 0; i < excludePatternMatchers.length; i++) {
        String pattern = EXCLUDE_EXT_PATTERNS[i].trim();
        if(pattern.length() == 0) continue;
        excludePatternMatchers[i] = Pattern.compile(pattern);
      }
    }
    
    public URL[] getUrls() { return urls.values().toArray(new URL[urls.size()]); }
    
    public void extract(URL url, WDataExtractContext ctx) throws Exception {
      URLParser urlParser = new URLParser(url.getUrl());
      XPathStructure structure = ctx.getXpathStructure();
      String baseURL = structure.findBase();
      if (baseURL == null || baseURL.length() == 0) {
        baseURL = urlParser.getBaseURL();
      }

      List<XPath> linkXPaths = structure.findAllLinks();
      for (int i = 0; i < linkXPaths.size(); i++) {
        XPath linkXPath = linkXPaths.get(i);
        String anchorText = linkXPath.getText();
        if(StringUtil.isEmpty(anchorText)) continue;
        String newURL = urlRewriter.rewrite(urlParser.getSiteURL(), baseURL, linkXPath.getNode().attr("href"));
        if (!isAllowProtocol(newURL)) continue;
        URLParser newURLNorm = new URLParser(newURL);
        URL_CLEANER.process(newURLNorm) ;
        
        if(isExclude(newURLNorm.getPath())) continue;
        
        if(!siteContext.allowDomain(newURLNorm))  continue; // ignore the external link
        
        URLPattern urlPattern = siteContext.matchesIgnoreURLPattern(newURLNorm);
        if(urlPattern != null && urlPattern.getType() == URLPattern.Type.ignore) {
          continue;
        }
        
        if (newURLNorm.getRef() != null) continue;
        
        String newNormalizedURL = newURLNorm.getNormalizeURL();
        if(urls.containsKey(newNormalizedURL)) continue;
        urls.put(newNormalizedURL,  new URL(anchorText, newNormalizedURL, 1));
      }
    }
    
    private boolean isExclude(String path) {
      if (excludePatternMatchers == null) return false;
      for (Pattern sel : excludePatternMatchers) {
        if (sel.matcher(path).matches()) return true;
      }
      return false;
    }
    
    private boolean isAllowProtocol(String url) {
      if (url.startsWith("http://")) return true ;
      if (url.startsWith("https://")) return true;
      return false;
    }
  }
}