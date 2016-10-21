package net.datatp.xhtml.extract;

import org.jsoup.nodes.Document;

import net.datatp.util.URLInfo;
import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.NodeCleaner;
import net.datatp.xhtml.xpath.NodeCleanerVisitor;
import net.datatp.xhtml.xpath.XPathStructure;

public class WDataContext {
  private URLInfo        urlInfo;
  private WData          wdata;
  private XPathStructure xpathStructure;

  public WDataContext(WData wdata) {
    this.wdata   = wdata;
  }
  
  public URLInfo getURInfo() {
    if(urlInfo == null) urlInfo = new URLInfo(wdata.getUrl());
    return urlInfo;
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
    xpathStructure = null;
  }
}