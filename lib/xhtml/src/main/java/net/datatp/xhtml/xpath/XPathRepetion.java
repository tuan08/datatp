package net.datatp.xhtml.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.datatp.util.text.StringUtil;

public class XPathRepetion {
  private String              xpathWithAncestorIndex;
  private String              parentXPath;
  private List<XPath>         holder = new ArrayList<>();
  private Map<String, String> attrs  = new HashMap<>();
  private Info                info   = null;

  public XPathRepetion(String xpathWithAncestorIndex) {
    this.xpathWithAncestorIndex = xpathWithAncestorIndex;
    int lastIndexOfSlash = xpathWithAncestorIndex.lastIndexOf('/');
    if(lastIndexOfSlash > 0) {
      parentXPath = xpathWithAncestorIndex.substring(0, lastIndexOfSlash);
    }
  }
  
  public String getXPathWithAncestorIndex() { return xpathWithAncestorIndex; }
  
  public String getParentXPath() { return parentXPath; }
  
  public List<XPath> getXPaths() { return holder ; }
  
  public void add(XPath xpath) { holder.add(xpath); }
  
  public int countRepetion() { return holder.size(); }
  
  public Info getInfo() {
    if(info != null) return info;
    info = new Info();
    double linkPresentCount = 0;
    double linkTextDensitySum = 0;
    int    totalTextLength = 0;
    int    maxDepth = 0;
    for(int i = 0; i < holder.size(); i++) {
      XPath xpath = holder.get(i);
      XPathInfo xpathInfo = xpath.getXPathInfo();
      if(xpathInfo.getLinkCount() > 0) linkPresentCount++;
      linkTextDensitySum += xpathInfo.getLinkTextDensity();
      totalTextLength += xpathInfo.getTextLenght();
      if(xpathInfo.getMaxDepth() > maxDepth) maxDepth = xpathInfo.getMaxDepth();
    }
    if(linkPresentCount > 0) info.setLinkPresentRatio(linkPresentCount/holder.size());
    if(linkTextDensitySum > 0) info.setAvgLinkTextDensity(linkTextDensitySum/holder.size());
    if(totalTextLength > 0) info.setAvgTextLength(totalTextLength/holder.size());
    info.setTotalTextLength(totalTextLength);
    info.setMaxDepth(maxDepth);
    return info;
  }
  
  public boolean hasAttr(String name, String[] values) {
    String val = attrs.get(name);
    if(val == null) return false;
    return StringUtil.isIn(val, values);
  }
  
  public String attr(String name) { return attrs.get(name) ; }
  
  public void attr(String name, String value) {
    attrs.put(name, value);
    XPath xpath = holder.get(0);
    xpath.getNode().parent().attr(name, value);
  }
  
  static public class Info {
    private double linkPresentRatio;
    private double avgLinkTextDensity = 0;
    private int    avgTextLength = 0;
    private int    totalTextLength = 0;
    private int    maxDepth        = 0;
    
    public double getLinkPresentRatio() { return linkPresentRatio; }
    public void setLinkPresentRatio(double linkPresentRatio) { this.linkPresentRatio = linkPresentRatio;}
    
    public double getAvgLinkTextDensity() { return avgLinkTextDensity; }
    public void setAvgLinkTextDensity(double density) { this.avgLinkTextDensity = density; }
    
    public int getAvgTextLength() { return avgTextLength; }
    public void setAvgTextLength(int avgTextLength) { this.avgTextLength = avgTextLength; }
    
    public int getTotalTextLength() { return totalTextLength;}
    public void setTotalTextLength(int totalTextLength) { this.totalTextLength = totalTextLength; }
    
    public int getMaxDepth() { return maxDepth; }
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }
  }
}