package net.datatp.crawler.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.site.WebPageType;
import net.datatp.crawler.site.analysis.WebPageTypeAnalyzer;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumFactory;
import net.datatp.util.URLInfo;
import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.extract.WDataContext;
import net.datatp.xhtml.util.URLRewriter;
import net.datatp.xhtml.util.URLSessionIdCleaner;
import net.datatp.xhtml.xpath.XPath;
import net.datatp.xhtml.xpath.XPathStructure;
/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com 
 *          Jun 23, 2010
 */
public class URLExtractor {
  final static Logger              logger          = LoggerFactory.getLogger(URLExtractor.class);
  final static URLSessionIdCleaner URL_CLEANER     = new URLSessionIdCleaner();

  private Pattern[]                excludePatternMatchers = {};
  private URLRewriter              urlRewriter     = new URLRewriter();
  protected URLDatumFactory        urlDatumFactory;
  
  public URLExtractor() {}
  
  public URLExtractor(URLDatumFactory urlDatumFactory, String ... patterns) {
    this.urlDatumFactory = urlDatumFactory;
    setExcludePatterns(Arrays.asList(patterns));
  }
  
  public void setExcludePatterns(List<String> excludePatterns) {
    excludePatternMatchers = new Pattern[excludePatterns.size()];
    for (int i = 0; i < excludePatternMatchers.length; i++) {
      String pattern = excludePatterns.get(i).trim();
      if(pattern.length() == 0) continue;
      excludePatternMatchers[i] = Pattern.compile(pattern);
    }
  }
  
  public Map<String, URLDatum> extract(URLContext urlCtx, WDataContext wdataCtx) {
    URLDatum urlDatum = urlCtx.getURLDatum();
    XPathStructure structure = wdataCtx.getXpathStructure();
    Map<String, URLDatum> urls = new HashMap<>();
    try {
      if(urlCtx == null) return urls ;
      String siteURL = urlCtx.getUrlParser().getSiteURL();
      String baseURL = structure.findBase();
      if (baseURL == null || baseURL.length() == 0) {
        baseURL = urlCtx.getUrlParser().getBaseURL();
      }
      
      if(urlDatum.getDeep() == 1) {
        String refreshUrl = structure.findRefreshMetaNodeUrl() ;
        if(refreshUrl != null) {
          URLDatum newURLDatum = 
            createURLDatum(urlDatum, refreshUrl, new URLInfo(refreshUrl), wdataCtx.getWdata().getAnchorText(), WebPageType.list);
          addURL(urls, refreshUrl, newURLDatum);
        }
      }

      List<XPath> linkXPaths = structure.findAllLinks();
      for (int i = 0; i < linkXPaths.size(); i++) {
        XPath linkXPath = linkXPaths.get(i);
        String anchorText = linkXPath.getText();
        if(StringUtil.isEmpty(anchorText)) continue;
        String newURL = urlRewriter.rewrite(siteURL, baseURL, linkXPath.getNode().attr("href"));
        if (!isAllowProtocol(newURL)) continue;
        URLInfo newURLInfo = new URLInfo(newURL);
        URL_CLEANER.process(newURLInfo) ;
        String newNormalizedURL = newURLInfo.getNormalizeURL();

        if (newURLInfo.getRef() != null) continue;

        if (!urlCtx.getSiteContext().allowDomain(newURLInfo)) continue; // ignore the external link

        if (isExclude(newURLInfo.getPath())) continue;
        
        WebPageTypeAnalyzer wpAnalyzer = urlCtx.getSiteContext().getWebPageAnalyzer().getWebPageTypeAnalyzer();
        WebPageType wpType = wpAnalyzer.analyze(anchorText, newURLInfo);
        if(wpType == WebPageType.ignore) continue;
        
        // CONTROL DEEP LIMIT
        int maxCrawlDeep = urlCtx.getSiteContext().getSiteConfig().getCrawlDeep();

        URLDatum newURLDatum = createURLDatum(urlDatum, newNormalizedURL, newURLInfo, anchorText, wpType);
        if (!isInDeepRange(newURLDatum, maxCrawlDeep)) continue;
        addURL(urls, newNormalizedURL, newURLDatum);
      }
    } catch (Throwable t) {
      logger.error("Cannot extract url for " + urlDatum.getOriginalUrl(), t);
    }
    return urls;
  }
  
  private void addURL(Map<String, URLDatum> urls, String url, URLDatum datum) {
    URLDatum exist = urls.get(url) ;
    if(exist == null) {
      urls.put(url, datum);
    } else {
      int anchorTextLength = datum.getAnchorText() == null ? 0 : datum.getAnchorText().length();
      int existAnchorTextLength = exist.getAnchorText() == null? 0 : exist.getAnchorText().length();
      if(anchorTextLength > existAnchorTextLength) urls.put(url, datum);
    }
  }

  private boolean isAllowProtocol(String url) {
    if (url.startsWith("http://")) return true ;
    if (url.startsWith("https://")) return true;
    return false;
  }

  private boolean isExclude(String path) {
    if (excludePatternMatchers == null) return false;
    for (Pattern sel : excludePatternMatchers) {
      if (sel.matcher(path).matches()) return true;
    }
    return false;
  }

  private boolean isInDeepRange(URLDatum datum, int maxDeep) { return datum.getDeep() <= maxDeep; }

  private URLDatum createURLDatum(URLDatum parent, String origUrl, URLInfo urlNorm, String anchorText, WebPageType wpType) {
    URLDatum urlDatum = urlDatumFactory.createInstance(System.currentTimeMillis());
    urlDatum.setOriginalUrl(origUrl, urlNorm);
    byte deep = (byte) (1 + parent.getDeep());
    urlDatum.setDeep(deep);
    urlDatum.setAnchorText(anchorText);
    if(wpType == WebPageType.detail)    urlDatum.setPageType(URLDatum.PAGE_TYPE_DETAIL);
    else if(wpType == WebPageType.list) urlDatum.setPageType(URLDatum.PAGE_TYPE_LIST);
    else                                urlDatum.setPageType(URLDatum.PAGE_TYPE_UNCATEGORIZED);
    return urlDatum;
  }
}