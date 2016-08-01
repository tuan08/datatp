package net.datatp.xhtml.xpath;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import net.datatp.xhtml.WData;

public class WDataContext {
  private WData                     wdata;
  private Document                  doc;
  private XPathStructure            xpathStructure;
  private Map<String, XPathExtract> extracts = new LinkedHashMap<>();

  public WDataContext(WData wdata) {
    this.wdata   = wdata;
    doc = wdata.createJsoupDocument();
    doc.traverse(new NodeCleanerVisitor(NodeCleaner.EMPTY_NODE_CLEANER, NodeCleaner.IGNORE_NODE_CLEANER));
    this.xpathStructure = new XPathStructure(doc);
  }
  
  public WDataContext(WData wdata, XPathStructure structure) {
    this.wdata = wdata;
    this.xpathStructure = structure;
  }
  
  public WData getWdata() { return wdata; }

  public Document getDocument() { return doc; }
  
  public XPathStructure getXpathStructure() { return xpathStructure; }

  public void save(XPathExtract extract) {
    extracts.put(extract.getName(), extract);
  }
  
  public XPathExtract[] getXPathExtract() {
    XPathExtract[] array = new XPathExtract[extracts.size()];
    return extracts.values().toArray(array);
  }
}
