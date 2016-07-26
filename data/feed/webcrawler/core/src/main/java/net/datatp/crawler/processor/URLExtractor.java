package net.datatp.crawler.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.datatp.crawler.site.URLContext;
import net.datatp.crawler.urldb.URLDatum;
import net.datatp.crawler.urldb.URLDatumFactory;
import net.datatp.util.URLParser;
import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.XhtmlDocument;
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
  final static URLSessionIdCleaner URL_CLEANER     = new URLSessionIdCleaner();
  final static Logger              logger          = LoggerFactory.getLogger(URLExtractor.class);

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
  
  public Map<String, URLDatum> extract(URLDatum urldatum, URLContext context, XhtmlDocument xdoc, XPathStructure structure) {
    Map<String, URLDatum> urls = new HashMap<String, URLDatum>();
    try {
      if(context == null) return urls ;
      
      String siteURL = context.getUrlParser().getSiteURL();
      String baseURL = structure.findBase();
      if (baseURL == null || baseURL.length() == 0) {
        baseURL = context.getUrlParser().getBaseURL();
      }
      
      if(urldatum.getDeep() == 1) {
        String refreshUrl = findRefreshMetaNodeUrl(structure) ;
        if(refreshUrl != null) {
          URLDatum newURLDatum = createURLDatum(urldatum, refreshUrl, new URLParser(refreshUrl), "refresh url");
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
        URLParser newURLNorm = new URLParser(newURL);
        URL_CLEANER.process(newURLNorm) ;
        String newNormalizeURL = newURLNorm.getNormalizeURL();

        if (newURLNorm.getRef() != null) continue;

        if (!context.getSiteContext().allowURL(newURLNorm)) {
          continue; // ignore the external link
        }

        if (isExclude(newURLNorm.getPathWithParams())) {
          continue;
        }

        // CONTROL DEEP LIMIT
        int maxCrawlDeep = context.getSiteContext().getSiteConfig().getCrawlDeep();

        URLDatum newURLDatum = createURLDatum(urldatum, newNormalizeURL, newURLNorm, anchorText);
        if (!isInDeepRange(newURLDatum, maxCrawlDeep)) continue;
        addURL(urls, newNormalizeURL, newURLDatum);
      }
    } catch (Throwable t) {
      logger.error("Cannot extract url for " + urldatum.getFetchUrl(), t);
    }
    return urls;
  }
  
  String findRefreshMetaNodeUrl(XPathStructure structure) {
    Elements elements = structure.select("html > head > meta");
    for(Element meta : elements) {
      String httpEquiv = meta.attr("http-equiv") ;
      if("refresh".equalsIgnoreCase(httpEquiv)) {
        String content = meta.attr("content") ;
        if(content == null) continue ;
        String[] array = content.split(";") ;
        for(String selStr : array) {
          String normStr = selStr.trim().toLowerCase() ;
          if(normStr.startsWith("url")) {
            int idx = selStr.indexOf("=") ;
            return selStr.substring(idx + 1) ;
          }
        }
      }
    }
    return null ;
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

  private URLDatum createURLDatum(URLDatum parent, String origUrl, URLParser urlNorm, String anchorText) {
    URLDatum urlDatum = urlDatumFactory.createInstance(System.currentTimeMillis());
    urlDatum.setOriginalUrl(origUrl, urlNorm);
    byte deep = (byte) (1 + parent.getDeep());
    urlDatum.setDeep(deep);
    urlDatum.setAnchorText(anchorText);
    return urlDatum;
  }
}