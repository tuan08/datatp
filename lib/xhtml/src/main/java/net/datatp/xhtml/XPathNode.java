package net.datatp.xhtml;

import java.util.ArrayList;

import org.w3c.dom.Node;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 25, 2010  
 */
public class XPathNode {
  private String xpath ;
  private Node   node  ;
  private String normalizedText ;
  
  public XPathNode(Node node) {
    this.xpath = getXPath(node) ;
    this.node = node ;
  }
  
  public String getXPath() { return this.xpath ; }
  
  public Node getNode() { return this.node ; }

  public String getNormalizedText() { 
  	if(normalizedText == null) {
  		this.normalizedText = normalize(node.getTextContent()) ;
  	}
  	return this.normalizedText ;
  }
  
  static public String normalize(String text) {
    if(text == null) return null ;
    text = text.trim() ;
    char[] buf = text.toCharArray() ;
    char preChar = 0 ;
    StringBuilder b = new StringBuilder() ;
    for(int i = 0; i < buf.length; i++) {
      char c = buf[i] ;
      if(c == '\t') c = ' ' ;
      else if(c == '\n') c = ' ' ;
      else if(c == '\r') c = ' ' ;
      else if(c == 160) c = ' ' ;
      
      if(preChar == ' ' && c == ' ') {
      } else {
        b.append(c) ;
      }
      preChar = c ;
    }
    return b.toString().trim() ;
  }

  final static public String getXPath(Node node) {
    if (null == node) return null;
    ArrayList<Node> hierarchy = new ArrayList<Node>(50);
    while (node != null && node.getNodeType() != Node.DOCUMENT_NODE) {
      hierarchy.add(node);
      node = node.getParentNode();
    }

    StringBuilder b = new StringBuilder();
    for(int i = hierarchy.size() - 1; i >= 0; i--) {
    	node = hierarchy.get(i) ;
    	int index = 0 ;
      Node prevSibling = node.getPreviousSibling();
      int nodeType = node.getNodeType() ;
      String nodeName = node.getNodeName() ;
      while(prevSibling != null) {
        if (prevSibling.getNodeType() == nodeType) {
          if (prevSibling.getNodeName().equals(nodeName)) {
            index++;
          }
        }
        index++ ;
        prevSibling = prevSibling.getPreviousSibling();
      }
    	b.append('/').append(node.getNodeName()).append('[').append(index).append(']');
    }
    return b.toString() ;
  }
  
  static boolean equalXPath(String xpathA, String xpathB) {
    return (xpathA.toLowerCase().replaceAll("\\[\\d+\\]", "").equals(xpathB
        .toLowerCase().replaceAll("\\[\\d+\\]", ""))) ? true : false;
  }

  public static int getEditDistance(String xpathA, String xpathB) {
    String[] A = xpathA.split("/");
    String[] B = xpathB.split("/");
    int min = A.length;
    if (min > B.length)
      min = B.length;
    for (int i = 0; i < min - 1; i++) {
      if (!A[i].equals(B[i])
          && A[i].split("\\[")[0].equals(B[i].split("\\[")[0])) {
        if (A[i].replaceAll("[^\\d]+", "").trim().length() <= 0
            || B[i].replaceAll("[^\\d]+", "").trim().length() <= 0)
          break;
        int al = Integer.parseInt(A[i].replaceAll("[^\\d]+", ""));
        int bl = Integer.parseInt(B[i].replaceAll("[^\\d]+", ""));
        return Math.abs(al - bl);
      } else if (!A[i].equals(B[i]))
        break;
    }
    if (A.length > min)
      return Integer.MIN_VALUE;
    return Integer.MAX_VALUE;

  }

  public static boolean computingToReplace(String Old, String New) {
    String[] old_ = Old.split("/");
    String[] new_ = New.split("/");
    int min = old_.length;
    if (min > new_.length)
      min = new_.length;
    for (int i = 0; i < min - 1; i++) {
      if (!old_[i].equals(new_[i])
          && old_[i].split("\\[")[0].equals(new_[i].split("\\[")[0])) {
        if (old_[i].replaceAll("[^\\d]+", "").trim().length() <= 0
            || new_[i].replaceAll("[^\\d]+", "").trim().length() <= 0)
          break;
        double ol = Double.parseDouble(old_[i].replaceAll("[^\\d]+", ""));
        double ne = Double.parseDouble(new_[i].replaceAll("[^\\d]+", ""));
        if (ol > ne)
          return true;
        else
          return false;
      } else if (!old_[i].equals(new_[i]))
        break;
    }
    if (old_.length > min)
      return true;
    return false;

  }

  public static int editDistance(String s1, String s2) {
    if (s1.trim().length() == 0 || s2.trim().length() == 0) return Integer.MAX_VALUE;
    s1 = s1.replaceAll("/#text", "");
    s2 = s2.replaceAll("/#text", "");
    String[] tokenS1 = s1.split("/");
    String[] tokenS2 = s2.split("/");
    int min = tokenS1.length;
    int max = tokenS2.length;
    if (min > tokenS2.length) {
      max = min;
      min = tokenS2.length;

    }
    int count = 0;
    for (int i = 0; i < min; i++) {
      if (tokenS1[i].equals(tokenS2[i])) {
        count++;
      } else
        break;
    }
    return (count);
  }

  public static int nearDistance(String s1, String s2) {
    if (s1.trim().length() == 0 || s2.trim().length() == 0) return Integer.MAX_VALUE;
    s1 = s1.replaceAll("/#text", "");
    s2 = s2.replaceAll("/#text", "");
    String[] tokenS1 = s1.split("/");
    String[] tokenS2 = s2.split("/");
    int min = tokenS1.length;
    int max = tokenS2.length;
    if (min > tokenS2.length) {
      max = min;
      min = tokenS2.length;
    }
    int count = 0;
    for (int i = 0; i < min; i++) {
      if (tokenS1[i].equals(tokenS2[i])) {
        count++;
      } else
        break;
    }
    return (max - count);
  }
}