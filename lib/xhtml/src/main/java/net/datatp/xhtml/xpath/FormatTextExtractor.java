package net.datatp.xhtml.xpath;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import net.datatp.util.text.StringUtil;

public class FormatTextExtractor {
  final static public String[] HEADER_TAG = {
    "title", "h1", "h2", "h3", "h4", "h5", "h6"
  };
  
  final static public String[] BLOCK_TAG = {
    "p", "tr", "caption", "cite", "pre"
  };
  
  public String extract(Node node) {
    StringBuilder b = new StringBuilder();
    traverse(b, node);
    return b.toString();
  }
  
  void traverse(StringBuilder b, Node node) {
    if(node instanceof TextNode) {
      String text = ((TextNode) node).text().trim();
      b.append(text);
    } else {
      List<Node> children = node.childNodes();
      for(int i = 0; i < children.size(); i++) {
        Node child = children.get(i);
        String tag = child.nodeName();
        if(StringUtil.isIn(tag, BLOCK_TAG)) {
          Element ele = (Element) child; 
          if(b.length() > 0 && b.charAt(b.length() - 1) != '\n') {
            b.append("\n");
          }
          b.append(ele.text().trim()).append("\n");
        } else if(StringUtil.isIn(tag, HEADER_TAG)) {
          Element ele = (Element) child; 
          if(b.length() > 0 && b.charAt(b.length() - 1) != '\n') b.append("\n");
          b.append("# ").append(ele.text().trim()).append("\n");
        } else {
          if("div".equals(tag)) {
            if(b.length() > 0 && b.charAt(b.length() - 1) != '\n') b.append("\n");;
          }
          traverse(b, child);
        }
      }
    }
  }
}