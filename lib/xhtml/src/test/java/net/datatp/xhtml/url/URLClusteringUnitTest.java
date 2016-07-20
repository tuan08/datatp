package net.datatp.xhtml.url;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.datatp.http.SimpleHttpFetcher;
import net.datatp.xhtml.XhtmlDocument;
import net.datatp.xhtml.util.HTMLUtils;

public class URLClusteringUnitTest {
  @Test
  public void testClustering() throws Exception {
    String url = "http://www.webtretho.com";
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    XhtmlDocument hdoc = fetcher.fetch(url);
    List<URLVector> urls = new ArrayList<URLVector>();

    Set<String> links = HTMLUtils.extractLinks(hdoc.getXhtml());
    for (String link : links) {
      urls.add(new URLVector(link));
    }

    URLClustering clustering = new URLClustering();
    Map<String, List<URLVector>> clusters = clustering.clustering(urls);
    for (Iterator<String> it = clusters.keySet().iterator(); it.hasNext();) {
      String key =  it.next();
      List<URLVector> cluster = clusters.get(key);
      System.out.println("Centroid: " + key);
      for (URLVector element : cluster) {
        System.out.println(element.getURL());
      }
      System.out.println("================================================");
    }
  }
}
