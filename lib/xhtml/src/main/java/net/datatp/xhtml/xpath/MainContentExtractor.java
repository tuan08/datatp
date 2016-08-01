package net.datatp.xhtml.xpath;

import java.util.List;

public class MainContentExtractor implements XPathExtractor {
  private String type =  "content"; 
  
  public MainContentExtractor() { }
  
  public MainContentExtractor(String type) { this.type = type; }
  
  
  @Override
  public  int extract(WDataContext context) {
    int extractCount = 0;
    XPathStructure structure = context.getXpathStructure();
    XPath titleXPath = structure.findTitleHeaderCandidate();
    XPath bodyXPath  = null;
    XPathRepetions xpathRepetitions = structure.getXPathRepetions();
    List<XPathRepetion> foundTextRepetions = 
      xpathRepetitions.findXPathRepetionWithTag("tag:text", "text:small", "text:medium", "text:small");
    if(titleXPath != null) {
      bodyXPath = findBody(structure, titleXPath, foundTextRepetions);
    } else {
      bodyXPath = findBody(structure, foundTextRepetions);
    }
    
    if(titleXPath != null) {
      XPathTree titleXPathTree = structure.getXPathTree().subTree(titleXPath);
      context.save(new XPathExtract(type + ":title", titleXPathTree.getXPathAsArray()));
      extractCount++;
    }
    
    if(bodyXPath != null) {
      XPathTree bodyXPathTree = structure.getXPathTree().subTree(bodyXPath);
      bodyXPathTree.removeXPathWithAttr("tag:repetion", new String[] { "link", "link:related" }, true);
      context.save(new XPathExtract(type + ":body", bodyXPathTree.getXPathAsArray()));
      extractCount++;
    }
    return extractCount;
  }

  XPath findBody(XPathStructure structure, XPath titleXPath, List<XPathRepetion> xpathRepetions) {
    XPath bestCandidateXPath = null;
    for(int i = 0; i < xpathRepetions.size(); i++) {
      XPathRepetion xpathRepetion = xpathRepetions.get(i);
      XPath blockXPath = xpathRepetion.getParentXPath();
      XPath candidateXPath = structure.findClosestAncestor(titleXPath, blockXPath);
      if(bestCandidateXPath == null) {
        bestCandidateXPath = candidateXPath;
      } else {
        if(candidateXPath.fragment.length > bestCandidateXPath.fragment.length) {
          bestCandidateXPath = candidateXPath;
        }
      }
    }
    return bestCandidateXPath;
  }
  
  XPath findBody(XPathStructure structure, List<XPathRepetion> xpathRepetions) {
    XPathRepetion bestXPathRepetion = null;
    for(int i = 0; i < xpathRepetions.size(); i++) {
      XPathRepetion xpathRepetion = xpathRepetions.get(i);
      if(bestXPathRepetion == null) {
        bestXPathRepetion = xpathRepetion;
      } else if(xpathRepetion.getInfo().getTotalTextLength() > bestXPathRepetion.getInfo().getTotalTextLength()) {
        bestXPathRepetion = xpathRepetion;
      }
    }
    XPath xpathBody = bestXPathRepetion.getParentXPath();
   return xpathBody;
  }
}
