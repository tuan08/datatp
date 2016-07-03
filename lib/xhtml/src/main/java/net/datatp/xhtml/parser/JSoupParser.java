package net.datatp.xhtml.parser;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import net.datatp.xhtml.dom.TDocumentBuilder;
import net.datatp.xhtml.dom.TNode;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JSoupParser implements TDocumentBuilder {
  final static public JSoupParser INSTANCE = new JSoupParser() ;

  public JSoupParser() {
  }

  public Document parse(String html) {
    Document doc = Jsoup.parse(html);
    return doc ;
  }

  public String reformat(String html) throws IOException {
    return html ;
  }

  public TNode toTNode(String html) {
    Document doc = Jsoup.parse(html);
    return buildTNode(null, doc) ;
  }

  private TNode buildTNode(TNode parent, Node node) {
    TNode tnode = new TNode(parent) ;
    tnode.setNodeName(node.nodeName()) ;
    if(node instanceof TextNode) {
      tnode.setNodeValue(((TextNode)node).text()) ;
    }
    String nodeName = tnode.getNodeName() ;
    if("meta".equalsIgnoreCase(nodeName)) {
      tnode.addAttribute("name", node.attr("name")) ;
      tnode.addAttribute("http-equiv", node.attr("http-equiv")) ;
      tnode.addAttribute("content", node.attr("content")) ;
    }

    if("base".equalsIgnoreCase(nodeName)) {
      tnode.addAttribute("href", node.attr("href")) ;
    }

    if("a".equalsIgnoreCase(nodeName)) {
      tnode.addAttribute("href", node.attr("href")) ;
      tnode.addAttribute("title", node.attr("title")) ;
    }
    if("img".equalsIgnoreCase(nodeName)) {
      tnode.addAttribute("src", node.attr("src")) ;
      tnode.addAttribute("alt", node.attr("alt")) ;
    }

    if(node instanceof Element) {
      Element ele = (Element) node ;
      tnode.setCssClass(ele.attr("class")) ;
      tnode.setElementId(ele.attr("id")) ;
    }
    List<Node> children = node.childNodes() ;
    for(int i = 0; i < children.size(); i++) {
      Node  nchild = children.get(i) ;
      if(nchild instanceof TextNode || nchild instanceof Element) {
        TNode child = buildTNode(tnode, children.get(i)) ;
        tnode.addChild(child) ;
      }
    }
    return tnode ;
  }
}