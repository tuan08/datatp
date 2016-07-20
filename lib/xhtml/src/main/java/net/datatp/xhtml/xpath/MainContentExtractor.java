package net.datatp.xhtml.xpath;

import java.util.List;

import org.jsoup.nodes.TextNode;

import net.datatp.util.text.CosineSimilarity;

public class MainContentExtractor implements XpathExtractor {

  @Override
  public XPathExtract[] extract(XPathStructure structure) {
    String title      = structure.findTitle();
    String anchorText = structure.getAnchorText();
    if(title == null) {
      title = anchorText;
    } else if(anchorText != null) {
      if(title.indexOf(structure.getAnchorText()) >= 0) {
        title = anchorText;
      }
    }
    XPath titleXPath = findTitleXPath(structure, title);
    XPath bodyXPath  = null;
    XPathRepetions xpathRepetitions = structure.getXPathRepetions();
    List<XPathRepetion> foundTextRepetions = 
      xpathRepetitions.findXPathRepetionsWithTag("tag:text", "text:small", "text:medium", "text:small");
    if(titleXPath != null) {
      bodyXPath = findBody(structure, titleXPath, foundTextRepetions);
    } else {
      bodyXPath = findBody(structure, foundTextRepetions);
    }
    XPathTree titleXPathTree = structure.getXPathTree().subTree(titleXPath);
    XPathTree bodyXPathTree = structure.getXPathTree().subTree(bodyXPath);
    bodyXPathTree.removeXPathWithAttr("tag:repetion", new String[] { "link", "link:related" }, true);
    
    XPathExtract[] extract = {
      new XPathExtract("title", titleXPathTree.getFlatXPaths()), new XPathExtract("body", bodyXPathTree.getFlatXPaths()), 
    };
    return extract;
  }

  private XPath findTitleXPath(XPathStructure structure, String title) {
    XPath titleXPath = null;
    for(XPath xpath : structure.getXPathTree().getFlatXPaths()) {
      if(xpath.getSection() != XPath.Section.Body) continue;
      if(xpath.isTextNode()) {
        String text = ((TextNode)xpath.getNode()).text();
        if(CosineSimilarity.INSTANCE.similarity(text, title) > 0.75) {
          titleXPath = xpath;
          break;
        }
      }
    }
    return titleXPath;
  }
  
  XPath findBody(XPathStructure structure, XPath titleXPath, List<XPathRepetion> xpathRepetions) {
    XPath bestCandidateXPath = null;
    for(int i = 0; i < xpathRepetions.size(); i++) {
      XPathRepetion xpathRepetion = xpathRepetions.get(i);
      XPath blockXPath = structure.getXPath(xpathRepetion.getParentXPath());
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
    XPath xpathBody = structure.getXPath(bestXPathRepetion.getParentXPath());
   return xpathBody;
  }
}
