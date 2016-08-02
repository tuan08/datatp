package net.datatp.xhtml.extract;

import org.jsoup.nodes.Document;

import net.datatp.xhtml.WData;
import net.datatp.xhtml.xpath.NodeCleaner;
import net.datatp.xhtml.xpath.NodeCleanerVisitor;
import net.datatp.xhtml.xpath.XPathStructure;

public class WDataExtractContext {
  private WData                     wdata;
  private Document                  doc;
  private XPathStructure            xpathStructure;

  public WDataExtractContext(WData wdata) {
    this.wdata   = wdata;
    doc = wdata.createJsoupDocument();
    doc.traverse(new NodeCleanerVisitor(NodeCleaner.EMPTY_NODE_CLEANER, NodeCleaner.IGNORE_NODE_CLEANER));
    this.xpathStructure = new XPathStructure(doc);
  }
  
  public WDataExtractContext(WData wdata, XPathStructure structure) {
    this.wdata = wdata;
    this.xpathStructure = structure;
  }
  
  public WData getWdata() { return wdata; }

  public Document getDocument() { return doc; }
  
  public XPathStructure getXpathStructure() { return xpathStructure; }
}
