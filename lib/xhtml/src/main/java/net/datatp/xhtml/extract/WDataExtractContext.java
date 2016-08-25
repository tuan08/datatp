package net.datatp.xhtml.extract;

import org.jsoup.nodes.Document;

import net.datatp.util.URLAnalyzer;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.NodeCleaner;
import net.datatp.xhtml.xpath.NodeCleanerVisitor;
import net.datatp.xhtml.xpath.XPathStructure;

public class WDataExtractContext {
  private WData          wdata;
  private URLAnalyzer    urlAnalyzer;
  private XPathStructure xpathStructure;

  public WDataExtractContext(WData wdata) {
    this.wdata   = wdata;
  }
  
  public WDataExtractContext(WData wdata, XPathStructure structure) {
    this.wdata = wdata;
    this.xpathStructure = structure;
  }

  public URLAnalyzer getURLAnalyzer() {
    if(urlAnalyzer == null) urlAnalyzer = new URLAnalyzer(wdata.getUrl());
    return urlAnalyzer;
  }
  
  public WData getWdata() { return wdata; }

  public Document createDocument() { return wdata.createJsoupDocument(); }
  
  public XPathStructure getXpathStructure() { 
    if(xpathStructure == null) {
      Document doc = wdata.createJsoupDocument();
      doc.traverse(new NodeCleanerVisitor(NodeCleaner.EMPTY_NODE_CLEANER, NodeCleaner.IGNORE_NODE_CLEANER));
      xpathStructure = new XPathStructure(doc);
    }
    return xpathStructure; 
  }
  
  public void reset() {
    urlAnalyzer = null;
    xpathStructure = null;
  }
}