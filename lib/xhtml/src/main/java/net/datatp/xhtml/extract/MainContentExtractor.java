package net.datatp.xhtml.extract;

import java.util.List;

import net.datatp.xhtml.extract.entity.ContentEntity;
import net.datatp.xhtml.xpath.XPath;
import net.datatp.xhtml.xpath.XPathRepetion;
import net.datatp.xhtml.xpath.XPathRepetions;
import net.datatp.xhtml.xpath.XPathStructure;
import net.datatp.xhtml.xpath.XPathTree;

public class MainContentExtractor implements WDataExtractor {
  private String type =  "content"; 
  
  public MainContentExtractor() { }
  
  public MainContentExtractor(String type) { this.type = type; }
  
  
  @Override
  public WDataExtract extract(WDataExtractContext context) {
    XPathStructure structure = context.getXpathStructure();
    XPath titleXPath = structure.findTitleHeaderCandidate();
    
    XPathRepetions xpathRepetitions = structure.getXPathRepetions();
    List<XPathRepetion> foundTextRepetions = 
        xpathRepetitions.findXPathRepetionWithTag("tag:text", "text:small", "text:medium", "text:small");
    XPath bodyXPath = findBody(structure, titleXPath, foundTextRepetions);
    if(bodyXPath == null) return null;

    WDataExtract extract = new WDataExtract(type);
    if(titleXPath != null) {
      XPathTree titleXPathTree = structure.getXPathTree().subTree(titleXPath);
      extract.add(new XPathExtract("title", titleXPathTree.getXPathAsArray()));
    }

    XPathTree bodyXPathTree = structure.getXPathTree().subTree(bodyXPath);
    bodyXPathTree.removeXPathWithAttr("tag:repetion", new String[] { "link", "link:related" }, true);
    extract.add(new XPathExtract("content", bodyXPathTree.getXPathAsArray()));
    return extract;
  }
  
  public ContentEntity extractEntity(WDataExtractContext context) {
    XPathStructure structure = context.getXpathStructure();
    XPath titleXPath = structure.findTitleHeaderCandidate();
    
    XPathRepetions xpathRepetitions = structure.getXPathRepetions();
    List<XPathRepetion> foundTextRepetions = 
        xpathRepetitions.findXPathRepetionWithTag("tag:text", "text:small", "text:medium", "text:small");
    XPath bodyXPath = findBody(structure, titleXPath, foundTextRepetions);
    if(bodyXPath == null) return null;

    ContentEntity entity = new ContentEntity();
    entity.setType(type);

    if(titleXPath != null) {
      XPathTree titleXPathTree = structure.getXPathTree().subTree(titleXPath);
      entity.setTitle(titleXPathTree.getText());
    }

    XPathTree bodyXPathTree = structure.getXPathTree().subTree(bodyXPath);
    bodyXPathTree.removeXPathWithAttr("tag:repetion", new String[] { "link", "link:related" }, true);
    entity.setContent(bodyXPathTree.getText());
    return entity;
  }

  XPath findBody(XPathStructure structure, XPath titleXPath, List<XPathRepetion> xpathRepetions) {
    if(titleXPath != null) {
      XPath bestCandidateXPath = null;
      for(int i = 0; i < xpathRepetions.size(); i++) {
        XPathRepetion xpathRepetion = xpathRepetions.get(i);
        XPath blockXPath = xpathRepetion.getParentXPath();
        XPath candidateXPath = structure.findClosestAncestor(titleXPath, blockXPath);
        if(bestCandidateXPath == null) {
          bestCandidateXPath = candidateXPath;
        } else {
          if(candidateXPath.getFragment().length > bestCandidateXPath.getFragment().length) {
            bestCandidateXPath = candidateXPath;
          }
        }
      }
      return bestCandidateXPath;
    } else {
      XPathRepetion bestXPathRepetion = null;
      for(int i = 0; i < xpathRepetions.size(); i++) {
        XPathRepetion xpathRepetion = xpathRepetions.get(i);
        if(bestXPathRepetion == null) {
          bestXPathRepetion = xpathRepetion;
        } else if(xpathRepetion.getInfo().getTotalTextLength() > bestXPathRepetion.getInfo().getTotalTextLength()) {
          bestXPathRepetion = xpathRepetion;
        }
      }
      if(bestXPathRepetion == null) return null;
      XPath xpathBody = bestXPathRepetion.getParentXPath();
      return xpathBody;
    }
  }
}
