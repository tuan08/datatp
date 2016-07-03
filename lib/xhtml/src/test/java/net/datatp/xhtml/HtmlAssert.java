package net.datatp.xhtml;

import junit.framework.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 27, 2010  
 */
public class HtmlAssert {
  static public void assertLink(Document doc, String id, String expectURL) {
    Node node = doc.getElementById(id) ;
    Assert.assertNotNull(node) ;
    Element ele = (Element) node ;
    Assert.assertEquals(expectURL, ele.getAttribute("href")) ;
  }
  
  static public void assertImgSrc(Document doc, String id, String expectURL) {
    Node node = doc.getElementById(id) ;
    Assert.assertNotNull(node) ;
    Element ele = (Element) node ;
    Assert.assertEquals(expectURL, ele.getAttribute("src")) ;
  }
  
  static public void assertScriptSrc(Document doc, String id, String expectURL) {
    Node node = doc.getElementById(id) ;
    Assert.assertNotNull(node) ;
    Element ele = (Element) node ;
    Assert.assertEquals(expectURL, ele.getAttribute("src")) ;
  }
  
  static public void assertNodeContent(Document doc, String id, String expectContent) {
    Node node = doc.getElementById(id) ;
    Assert.assertNotNull(node) ;
    Assert.assertEquals(expectContent, node.getTextContent()) ;
  }
  
  static public void assertNoNode(Document doc, String id) {
    Node node = doc.getElementById(id) ;
    Assert.assertNull(node) ;
  }
  
  static public void assertNode(Document doc, String id) {
    Node node = doc.getElementById(id) ;
    Assert.assertNotNull(node) ;
  }
}