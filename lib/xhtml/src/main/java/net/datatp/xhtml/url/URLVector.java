package net.datatp.xhtml.url;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.datatp.util.URLParser;
import net.datatp.util.text.NumberUtil;
import net.datatp.util.text.StringUtil;

public class URLVector {
  private String url;
  private URLParser urlnormalizer ;
  private String seed = "";
  private Object object ;
  
  public URLVector(String url) {
    this.url = url;
    try {
      urlnormalizer = new URLParser(URLDecoder.decode(url.toLowerCase(), "UTF-8"));
    } catch (Exception e) {
    	throw new RuntimeException(e) ;
    }
  }

  public URLVector(String url, Object object) {
  	this(url) ;
  	this.object = object ;
  }
  
  public String getURL() { return url; }

  public <T> T getObject() { return (T) object ; }
  
  public int getDeep() {
    return getURLNormalizer().getPathSegments().size();
  }

  public int getNumParams() {
    if (getURLNormalizer().getParams() == null)
      return 0;
    
    return getURLNormalizer().getParams().size();
  }

  public String getSeed() { return seed; }
  public void setSeed(String seed) { this.seed = seed; }

  public URLParser getURLNormalizer() {
  	return urlnormalizer ;
  }

  public String getHost() {
    return getURLNormalizer().getHost();
  }

  public String[] getVector() {
    List<String> features = new ArrayList<String>();

    URLParser normalizer = getURLNormalizer();
    List<String> segs = normalizer.getPathSegments();

    features.add("deep:" + segs.size());

    for (int i = 0; i < segs.size(); i++) {
      String seg = segs.get(i);

      if (i == segs.size() - 1) {
        if (segs.get(i).contains(".")) {
          features.add("ext:" + seg.substring(seg.lastIndexOf(".") + 1) + ":deep:" + (i + 1));
          seg = seg.substring(0, seg.lastIndexOf("."));
        }
      }

      if (!seg.equals(""))
        if (NumberUtil.isDigits(seg)) {
          /*
           * if (seg.length() > 2) { features.add("pathnumber:large" + ":deep:"
           * + (i + 1)); } else if (Integer.parseInt(seg) > 10) {
           * features.add("pathnumber:large" + ":deep:" + (i + 1)); } else {
           * features.add("pathnumber:small" + ":deep:" + (i + 1)); }
           */
          features.add("pathnumber" + ":deep:" + (i + 1));

        } else if (seg.contains("-") || seg.contains("_") || seg.contains("+")) {
          String[] tokens = seg.split("[-_+]");
          if (tokens.length > 2) {
            features.add("pathMark:true" + ":deep:" + (i + 1));
          } else {
            features.add(seg + ":deep:" + (i + 1));
          }

        } else {
          features.add(seg + ":deep:" + (i + 1));
        }
    }

    Map<String, String[]> params = normalizer.getParams();
    if (params != null) {
      Set<String> keys = params.keySet();

      for (String key : keys) {
        features.add("param:" + key);
        String[] values = params.get(key);
        for (int i = 0; i < values.length; i++) {
          if (!values[i].equals(""))
            if (!NumberUtil.isDigits(values[i])) {
              features.add(values[i]);
            } else {
              /*
               * if (values[i].length() > 2) { features.add("paramvalue:large");
               * } else if (Integer.parseInt(values[i]) > 10) {
               * features.add("paramvalue:large"); } else {
               * features.add("paramvalue:small"); }
               */
              features.add(key + ":frequency");
            }
        }
      }
    }

    return StringUtil.toArray(features);
  }

  public static void main(String[] args) throws Exception {
    String url = "http://giaoduc.edu.vn/news/tin-tuc-667/10212/hoa-hoan-tai-cong-trinh-toa-nha-cao-nhat-viet-nam-168395.aspx?ArticleId=10000";
    URLVector vector = new URLVector(url);
    String[] features = vector.getVector();
    for (String feature : features) {
      System.out.println(feature);
    }
  }
}
