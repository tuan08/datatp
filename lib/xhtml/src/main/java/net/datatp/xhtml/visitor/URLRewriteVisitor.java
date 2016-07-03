package net.datatp.xhtml.visitor;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.datatp.xhtml.util.URLRewriter;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class URLRewriteVisitor extends NodeVisitor {
  private String  siteURL ;
  private String  baseURL ;
  private URLRewriter rewriter ;
  
  public URLRewriteVisitor(String  siteURL, String  baseURL) {
    this.siteURL = siteURL ;
    if(baseURL.endsWith("/")) baseURL = baseURL.substring(0, baseURL.length() - 1) ;
    this.baseURL = baseURL ;
    this.rewriter = new URLRewriter() ;
  }
  
  public void preTraverse(Node node) {
    String nodeName = node.getNodeName() ;
    if("a".equals(nodeName)) rewriteLink(node) ;     
    else if("img".equals(nodeName)) rewriteSrc(node) ;
    else if("link".equals(nodeName)) rewriteLink(node) ;
    else if("script".equals(nodeName)) rewriteSrc(node) ; 
  }
  
  public void postTraverse(Node node) {
  }
  
  private void rewriteLink(Node node) {
    Element ele = (Element) node ;
    String href = ele.getAttribute("href") ;
    if(href == null || href.isEmpty()) return ;
    //external link
    if(href.startsWith("http://")) return  ;
    String newURL = rewriter.rewrite(siteURL, baseURL, href) ;
    ele.setAttribute("href", newURL) ;
  }
  
  private void rewriteSrc(Node node) {
    Element ele = (Element) node ;
    String src = ele.getAttribute("src") ;
    if(src == null || src.isEmpty()) return ;
    if(src.startsWith("http://")) return  ;
    String newURL = rewriter.rewrite(siteURL, baseURL, src) ;
    ele.setAttribute("src", newURL) ;
  }
}
