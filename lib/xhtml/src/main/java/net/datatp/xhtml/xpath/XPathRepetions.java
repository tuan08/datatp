package net.datatp.xhtml.xpath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XPathRepetions {
  private Map<String, XPathRepetion> holder = new LinkedHashMap<>();
  
  public void add(XPath xpath) {
    String xpathWithAncestorIndex = xpath.getXPathWithAncestorIndex();
    XPathRepetion block = holder.get(xpathWithAncestorIndex);
    if(block == null) {
      block = new XPathRepetion(xpathWithAncestorIndex);
      holder.put(xpathWithAncestorIndex, block);
    }
    block.add(xpath);
  }
  
  public void analyze(XPathStructure structure, XPathRepetionAnalyzer ... analyzer) {
    Iterator<XPathRepetion> i = holder.values().iterator();
    while(i.hasNext()) {
      XPathRepetion repetion = i.next();
      if(repetion.countRepetion() < 2) {
        i.remove();
        continue;
      }
      for(XPathRepetionAnalyzer sel : analyzer) sel.analyze(structure, repetion);
    }
  }
  
  public List<XPathRepetion> findXPathRepetionsWithTag(String tag, String ... value) {
    List<XPathRepetion> founds = new ArrayList<>();
    for(XPathRepetion sel : holder.values()) {
      if(sel.hasAttr(tag, value)) founds.add(sel);
    }
    return founds;
  }
  
  public String getFormattedText() {
    StringBuilder b = new StringBuilder();
    for(XPathRepetion sel : holder.values()) {
      if(b.length() > 0) b.append("\n");
      b.append(sel.getXPathWithAncestorIndex()).append(": repetion count = " + sel.countRepetion());
      String repetionTag = sel.attr("tag:repetion");
      if(repetionTag != null) {
        b.append(", repetion tag = " + repetionTag);
      }
      
      String textTag = sel.attr("tag:text");
      if(textTag != null) {
        b.append(", text tag = " + textTag);
      }
    }
    return b.toString();
  }
}