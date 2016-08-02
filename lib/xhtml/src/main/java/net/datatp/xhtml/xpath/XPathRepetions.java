package net.datatp.xhtml.xpath;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.datatp.util.HeapTree;

public class XPathRepetions {
  private Map<String, XPathRepetion> holder = new LinkedHashMap<>();
  
  public void add(XPathStructure structure, XPath xpath) {
    String xpathWithAncestorIndex = xpath.getXPathWithAncestorIndex();
    XPathRepetion block = holder.get(xpathWithAncestorIndex);
    if(block == null) {
      block = new XPathRepetion(structure, xpathWithAncestorIndex);
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
  
  public List<XPathRepetion> findXPathRepetionWithTag(String tag, String ... value) {
    List<XPathRepetion> founds = new ArrayList<>();
    for(XPathRepetion sel : holder.values()) {
      if(sel.hasAttr(tag, value)) founds.add(sel);
    }
    return founds;
  }
  
  public List<XPathRepetion> findXPathRepetionThatContains(XPath xpath) {
    String xpathWithAncestorIndex = xpath.getXPathWithAncestorIndex();
    List<XPathRepetion> founds = new ArrayList<>();
    for(XPathRepetion sel : holder.values()) {
      String ancestorXPath = sel.getParentXPath().getXPathWithAncestorIndex();
      if(xpathWithAncestorIndex.startsWith(ancestorXPath)) {
        founds.add(sel);
      }
    }
    return founds;
  }
  
  public List<ClosestXPathRepetion> findClosestXPathRepetion(XPath xpath, int max) {
    HeapTree<ClosestXPathRepetion> heapTree = new HeapTree<>(max, CLOSEST_XPATH_REPETION_COMPARATOR);
    for(XPathRepetion sel : holder.values()) {
      String commonAncestor = xpath.findClosestAncestor(sel.getParentXPath());
      heapTree.insert(new ClosestXPathRepetion(commonAncestor, sel));
    }
    List<ClosestXPathRepetion> founds = new ArrayList<>();
    ClosestXPathRepetion found = null;
    while((found = heapTree.removeTop()) != null) {
      founds.add(found);
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
  
  static public class ClosestXPathRepetion {
    final public String        commonAncestor;
    final public XPathRepetion xpathRepetion;
    
    ClosestXPathRepetion(String commonAncestor, XPathRepetion xpathRepetion) {
      this.commonAncestor = commonAncestor;
      this.xpathRepetion  = xpathRepetion;
    }
  }
  
  static Comparator<ClosestXPathRepetion> CLOSEST_XPATH_REPETION_COMPARATOR = new Comparator<ClosestXPathRepetion>() {
    @Override
    public int compare(ClosestXPathRepetion x1, ClosestXPathRepetion x2) {
      if(x1.commonAncestor.length() < x2.commonAncestor.length()) return -1;
      if(x1.commonAncestor.length() > x2.commonAncestor.length()) return  1;
      return 0;
    }
    
  };
}