package net.datatp.xhtml.xpath;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import net.datatp.util.text.StringUtil;

abstract public class NodeCleaner {
  static public NodeCleaner EMPTY_NODE_CLEANER  = new EmptyNodeCleaner();
  static public NodeCleaner IGNORE_NODE_CLEANER = new IgnoreNodeCleaner();
  
  abstract public boolean keep(Node node);
  
  
  static public class EmptyNodeCleaner extends NodeCleaner {
    @Override
    public boolean keep(Node node) {
      if(node instanceof TextNode) {
        TextNode tnode = (TextNode)node;
        String text = tnode.text().trim();
        if(text.isEmpty()) return false;
      }
      return true;
    }
  }
  
  static public class IgnoreNodeCleaner extends NodeCleaner {
    final static String[] IGNORE_NODE_NAME = { "script", "noscript", "meta", "link", "#comment", "input", "textarea", "button"};
    @Override
    public boolean keep(Node node) {
      if(StringUtil.isIn(node.nodeName(), IGNORE_NODE_NAME)) {
        return false;
      }
      return true;
    }
  }
}
