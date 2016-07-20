package net.datatp.xhtml.dom.extract;

import java.util.HashMap;
import java.util.Map;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class ExtractBlock {
  private String blockName ;
  private String[] tag ;
  private Map<String, ExtractNode> extractNodes = new HashMap<String, ExtractNode>() ;

  public ExtractBlock(String name) { this.blockName = name ; }

  public String getBlockName() { return blockName; }
  public void   setBlockName(String name) { this.blockName = name; }

  public String getTitle() { return this.getTextContent("title") ; }
  public void   setTitle(String title, Object node) { 
    add("title", title, node) ;
  }

  public String getDescription() { return this.getTextContent("description") ; }

  public String getContent() { return this.getTextContent("content") ; }
  public void   setContent(String content, Object node) { 
    add("content", content, node) ;
  }

  public String[] getTags() { return tag ; }
  public void setTags(String ... tag) { this.tag = tag ; }

  public int countExtractNode() { return this.extractNodes.size() ; }

  public String getTextContent(String nodeName) {
    ExtractNode node = extractNodes.get(nodeName) ;
    if(node == null) return null ;
    return node.getTextContent() ;
  }

  public void add(String name, String textContent, Object node) {
    if(textContent != null) textContent = textContent.trim() ;
    ExtractNode extractNode = new ExtractNode(name, textContent, node) ;
    extractNodes.put(name, extractNode) ;
  }

  public Map<String, ExtractNode> getExtractNodes() { return this.extractNodes ; }

  public String asFormatText() {
    StringBuilder b = new StringBuilder();
    b.append("Title: " + getTitle());
    b.append("Description: " + getDescription());
    b.append("Content: " + getContent());
    return b.toString();
  }
  
  static public class ExtractNode {
    private String name ;
    private String textContent ;
    private Object node ;

    public ExtractNode() {}

    public ExtractNode(String name, String textContent, Object node) {
      this.name = name ;
      this.textContent = textContent ;
      this.node = node ;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public Object getNode() { return node; }
    public void setNode(Object node) { this.node = node; }
  }
}