package net.datatp.xhtml.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.datatp.util.text.CosineSimilarity;

public class URLClustering {
  private double high_threshold = 0.85;
  private double low_threshold = 0.55;

  public Map<String, List<URLVector>> clustering(List<URLVector> urls) {
    Map<String, List<URLVector>> clusters = new HashMap<String, List<URLVector>>();

    for (int i = 0; i < urls.size(); i++) {
      if (urls.get(i).getSeed().equals("")) {
        List<URLVector> cluster = new ArrayList<URLVector>();
        cluster.add(urls.get(i));
        for (int j = i + 1; j < urls.size(); j++) {
          if (urls.get(j).getSeed().equals("")) {
            if (urls.get(i).getDeep() <= 4) {
              if (similarity(urls.get(i), urls.get(j)) >= low_threshold) {
                urls.get(j).setSeed(urls.get(i).getURL());
                cluster.add(urls.get(j));
              }
            } else {
              if (similarity(urls.get(i), urls.get(j)) >= high_threshold) {
                urls.get(j).setSeed(urls.get(i).getURL());
                cluster.add(urls.get(j));
              }
            }

          }
        }

        clusters.put(urls.get(i).getURL(), cluster);
      }
    }

    return clusters;
  }

  public double similarity(URLVector vector1, URLVector vector2) {
    CosineSimilarity cos = new CosineSimilarity();
    if (vector1.getHost().equals(vector2.getHost())
        && (vector1.getDeep() == vector2.getDeep())
        && (vector1.getNumParams() == vector2.getNumParams())) {
      return cos.similarity(vector1.getVector(), vector2.getVector());
    }
    return 0;
  }

  public static void main(String[] args) throws Exception {
    String url1 = "http://vnlp.net/blog/?author=5";
    String url2 = "http://vnlp.net/blog/?p=241#respond";

    URLVector vector1 = new URLVector(url1);
    URLVector vector2 = new URLVector(url2);

    URLClustering clustering = new URLClustering();
    System.out.println(clustering.similarity(vector1, vector2));

  }
}
