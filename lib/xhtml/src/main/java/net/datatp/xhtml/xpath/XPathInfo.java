package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

public class XPathInfo implements NodeVisitor {
  private int textLenght;
  private int linkTextLength;
  private int linkCount;
  private int minDepth = Integer.MAX_VALUE; 
  private int maxDepth = 0;
  
  public int getTextLenght() { return textLenght; }

  public int getLinkTextLength() { return linkTextLength; }
  
  public int getLinkCount() { return linkCount; }
  
  public int getMaxDepth() { return maxDepth - minDepth; }
  
  public double getLinkTextDensity() {
    if(textLenght == 0) return 0d ;
    return linkTextLength/textLenght;
  }
  
  @Override
  public void head(Node node, int depth) {
    
    if(depth < minDepth) minDepth = depth;
    if(depth > maxDepth) maxDepth = depth;
    if("a".equals(node.nodeName())) {
      linkCount++;
      Element a = (Element)node;
      linkTextLength += a.text().length();
    } else if(node instanceof TextNode) {
      textLenght += ((TextNode)node).text().length();
    }
  }
  
  @Override
  public void tail(Node node, int depth) {
  }
  
}
