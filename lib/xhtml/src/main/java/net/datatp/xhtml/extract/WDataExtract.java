package net.datatp.xhtml.extract;

import java.util.ArrayList;
import java.util.List;

import net.datatp.xhtml.extract.entity.ExtractEntity;

public class WDataExtract {
  private String             type;
  private List<XPathExtract> xpathExtracts = new ArrayList<>();
  private ExtractEntity      extractEntity;

  public WDataExtract(String name) {
    this.type = name;
  }

  public String getType() { return type; }
  public void setName(String name) { this.type = name; }

  public List<XPathExtract> getXPathExtracts() { return xpathExtracts; }
  public void setXpathExtracts(List<XPathExtract> xpathExtracts) { this.xpathExtracts = xpathExtracts; }
  
  public void add(XPathExtract xpathExtract) {
    if(xpathExtract == null) return;
    xpathExtracts.add(xpathExtract);
  }
  
  public String getFormattedText() {
    StringBuilder b = new StringBuilder();
    b.append("Name: " + type) ;
    for(int i = 0; i < xpathExtracts.size(); i ++) {
      b.append(xpathExtracts.get(i).getFormattedText()).append("\n");
    }
    return b.toString();
  }
  
  static public String format(List<WDataExtract> holder) {
    StringBuilder b = new StringBuilder();
    for(int i = 0; i < holder.size(); i++) {
      b.append(holder.get(i).getFormattedText()).append("\n");
    }
    return b.toString();
  }
}