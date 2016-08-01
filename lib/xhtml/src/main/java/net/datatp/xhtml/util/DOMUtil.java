package net.datatp.xhtml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.datatp.util.URLParser;
import net.datatp.util.text.matcher.StringExpMatcher;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 26, 2010  
 */
public class DOMUtil {
  static public String getTitle(Document doc)  {
    Node title = findFirstNodeByTagName(doc, "title") ;
    if(title != null) return title.getTextContent();
    return null ;
  }

  static public String getBase(Document doc)  {
    Node base = findFirstNodeByTagName(doc, "base") ;
    if(base == null) return null ;
    Element e = (Element) base ;
    return e.getAttribute("href") ;
  }

  static public void createBase(Document doc, String url)  {
    URLParser urlnorm = new URLParser(url) ;
    Element baseEle = doc.createElement("base") ;
    baseEle.setAttribute("href", urlnorm.getBaseURL()) ;
    Element head = (Element)findFirstNodeByTagName(doc, "head") ;
    Node firstNode = head.getFirstChild() ;
    if(firstNode.getNodeType() != Node.ELEMENT_NODE) {
      head.appendChild(baseEle) ;
    } else {
      head.insertBefore(firstNode, baseEle) ;
    }
    //head.appendChild(baseEle) ;
  }

  static public Node findFirstNodeByTagName(Node node, String tag) {
    if(node == null) return null ;
    if(tag.equalsIgnoreCase(node.getNodeName())) return node ;
    NodeList children = node.getChildNodes() ;
    for(int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i) ;
      Node ret = findFirstNodeByTagName(child, tag) ;
      if(ret != null) return ret ;
    }
    return null ;
  }

  static public Node[] findMetaNode(Document doc) {
    List<Node> holder = new ArrayList<Node>() ;
    findNodeByTagName(holder, doc, "meta", 0, 4) ;
    return holder.toArray(new Node[holder.size()]) ;
  }

  static public String findRefreshMetaNodeUrl(Document doc) {
    for(Node sel : findMetaNode(doc)) {
      Element ele = (Element) sel ;
      String httpEquiv = ele.getAttribute("http-equiv") ;
      if("refresh".equalsIgnoreCase(httpEquiv)) {
        String content = ele.getAttribute("content") ;
        if(content == null) continue ;
        String[] array = content.split(";") ;
        for(String selStr : array) {
          String normStr = selStr.trim().toLowerCase() ;
          if(normStr.startsWith("url")) {
            int idx = selStr.indexOf("=") ;
            return selStr.substring(idx + 1) ;
          }
        }
      }
    }
    return null ;
  }

  static public void findNodeByTagName(List<Node> holder, Node node, String tag, int deep, int maxDeep) {
    if(deep == maxDeep) return ; 
    if(node == null) return  ;
    if(tag.equalsIgnoreCase(node.getNodeName())) {
      holder.add(node) ;
    }
    NodeList children = node.getChildNodes() ;
    for(int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i) ;
      findNodeByTagName(holder, child, tag, deep + 1, maxDeep) ;
    }
  }

  static public Element getElement(Node node) {
    if(node instanceof Element) return (Element) node ;
    return (Element)node.getParentNode() ;
  }

  static public boolean hasAttr(Element e, String checkAttr, String[] attrs) {
    int backLevel = 0 ;
    while(e != null && backLevel < 3) {
      String attr = e.getAttribute(checkAttr) ;
      if(!isEmpty(attr)) {
        attr = attr.toLowerCase() ;
        for(String sel : attrs) {
          if(attr.indexOf(sel) >= 0) {
            return true ;
          }
        }
      }
      Node pnode = e.getParentNode() ;
      if(!(pnode instanceof Element)) return false ;
      e = (Element) pnode ;
      backLevel++ ;
    }
    return false ;
  }

  static public boolean hasAttr(Element e, String checkAttr, Pattern[] attrs, int checkBackLevel) {
    int backLevel = 0 ;
    while(e != null && backLevel < checkBackLevel) {
      String attr = e.getAttribute(checkAttr) ;
      if(!isEmpty(attr)) {
        attr = attr.toLowerCase() ;
        for(Pattern sel : attrs) {
          if(sel.matcher(attr).matches()) return true ;
        }
      }
      Node pnode = e.getParentNode() ;
      if(!(pnode instanceof Element)) return false ;
      e = (Element) pnode ;
      backLevel++ ;
    }
    return false ;
  }

  static public boolean hasAttr(Element e, String checkAttr, StringExpMatcher[] attrs, int checkBackLevel) {
    int backLevel = 0 ;
    while(e != null && backLevel < checkBackLevel) {
      String attr = e.getAttribute(checkAttr) ;
      if(!isEmpty(attr)) {
        attr = attr.toLowerCase() ;
        for(StringExpMatcher sel : attrs) {
          if(sel.matches(attr)) return true ;
        }
      }
      Node pnode = e.getParentNode() ;
      if(!(pnode instanceof Element)) return false ;
      e = (Element) pnode ;
      backLevel++ ;
    }
    return false ;
  }

  static public boolean matches(String string, Pattern[] attrs) {
    for(Pattern sel : attrs) {
      if(sel.matcher(string).matches()) return true ;
    }
    return false ;
  }

  static public boolean isActionElement(Element e) {
    int backLevel = 0 ;
    while(e != null && backLevel < 3) {
      if("a".equalsIgnoreCase(e.getNodeName())) return true ;
      String onclick = e.getAttribute("onclick") ;
      if(!isEmpty(onclick)) return true ;
      Node pnode = e.getParentNode() ;
      if(!(pnode instanceof Element)) return false ;
      e = (Element) pnode ;
      backLevel++ ;
    }
    return false ;
  }

  static public Element getAncestor(Node node, String ancestorType, int checkBackLevel) {
    int backLevel = 0 ;
    while(node != null && backLevel < checkBackLevel) {
      if(ancestorType.equalsIgnoreCase(node.getNodeName())) return (Element) node ;
      node = node.getParentNode() ;
      backLevel++ ;
    }
    return null ;
  }

  static public boolean hasAncestor(Element e, String tag) {
    int backLevel = 0 ;
    while(e != null && backLevel < 3) {
      if(tag.equalsIgnoreCase(e.getNodeName())) return true ;
      Node pnode = e.getParentNode() ;
      if(!(pnode instanceof Element)) return false ;
      e = (Element) pnode ;
      backLevel++ ;
    }
    return false ;
  }

  static public Element getAncestor(Element e, String tag, int limit) {
    int backLevel = 0 ;
    while(e != null && backLevel < limit) {
      if(tag.equalsIgnoreCase(e.getNodeName())) return e ;
      Node pnode = e.getParentNode() ;
      if(!(pnode instanceof Element)) return null ;
      e = (Element) pnode ;
      backLevel++ ;
    }
    return null ;
  }

  static public boolean hasParent(Node node, String tag) {
    Node parent = node.getParentNode() ;
    if(parent == null) return false ;
    return parent.getNodeName().equalsIgnoreCase(tag) ;
  }

  static public String getAncestorXPath(String xpath, int backLevel) {
    String retXPath = xpath ;
    while(backLevel > 0) {
      int idx = retXPath.lastIndexOf('/') ;
      retXPath = retXPath.substring(0, idx);
      backLevel-- ;
    }
    return retXPath ; 
  }

  static public boolean isEmpty(String s) {
    return s == null || s.length() == 0;
  }
}