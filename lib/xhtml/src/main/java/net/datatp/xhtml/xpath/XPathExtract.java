package net.datatp.xhtml.xpath;

import org.jsoup.nodes.TextNode;

public class XPathExtract {
  private String  name;
  private XPath[] xpaths;
  
  public XPathExtract(String name, XPath ... xpaths) {
    this.name   = name;
    this.xpaths = xpaths;
  }

  public String getName() { return name; }

  public XPath[] getXpaths() { return xpaths; }
  
  public String getFormattedText() {
    StringBuilder b = new StringBuilder();
    b.append(name).append(": ");
    for(XPath xpath : xpaths) {
      if(xpath.isTextNode()) {
        TextNode textNode = (TextNode) xpath.getNode();
        b.append(textNode.text()).append("\n");
      }
    }
    return b.toString();
  }
  
  static public String getFormattedText(XPathExtract ... extracts) {
    StringBuilder b = new StringBuilder();
    for(int i = 0; i < extracts.length; i++) {
      b.append(extracts[i].getFormattedText()).append("\n");
    }
    return b.toString();
  }
}
