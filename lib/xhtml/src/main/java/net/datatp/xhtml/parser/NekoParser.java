package net.datatp.xhtml.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.datatp.xhtml.dom.TDocumentBuilder;
import net.datatp.xhtml.dom.TNode;
/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 23, 2010  
 */
public class NekoParser implements TDocumentBuilder {
  final static public NekoParser INSTANCE = new NekoParser() ;

  private DOMParser nonWellFormParser = new DOMParser();
  private DocumentBuilder wellFormParser ;

  public NekoParser() {
    this.nonWellFormParser = new DOMParser();
    try {
      this.nonWellFormParser.setFeature("http://cyberneko.org/html/features/augmentations", true);
      this.nonWellFormParser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
      this.nonWellFormParser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
      //System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.crimson.jaxp.DocumentBuilderFactoryImpl") ;

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      wellFormParser = dbFactory.newDocumentBuilder();
      wellFormParser.setEntityResolver(new EntityResolver() {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
          //System.out.println("Ignoring " + publicId + ", " + systemId);
          return new InputSource(new StringReader(""));
        }
      });

      wellFormParser.setErrorHandler(new ErrorHandler(){
        public void error(SAXParseException arg0) throws SAXException {
        }

        public void fatalError(SAXParseException arg0) throws SAXException {
        }

        public void warning(SAXParseException arg0) throws SAXException {
        }
      });
    } catch(Exception ex) {
      ex.printStackTrace() ;
    }
  }

  public Document parse(String data) throws Exception {
    try {
      return wellFormParser.parse(new InputSource(new StringReader(data)));
    } catch(Throwable ex) {
      nonWellFormParser.parse(new InputSource(new StringReader(data)));
      return nonWellFormParser.getDocument() ;
    }
  }

  public Document parse(byte[] data) throws Exception {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data) ; 
      return wellFormParser.parse(bis);
    } catch(Throwable ex) {
      ByteArrayInputStream bis = new ByteArrayInputStream(data) ;
      nonWellFormParser.parse(new InputSource(bis));
      return nonWellFormParser.getDocument() ;
    }
  }

  public Document parseNonWellForm(String html) throws Exception {
    return parseNonWellForm(new StringReader(html)) ;
  }

  public Document parseNonWellForm(Reader reader) throws Exception {
    nonWellFormParser.parse(new InputSource(reader));
    return nonWellFormParser.getDocument() ;
  }

  public Document parseNonWellForm(InputStream is) throws Exception {
    nonWellFormParser.parse(new InputSource(is));
    return nonWellFormParser.getDocument() ;
  }

  public Document parseWellForm(String data) throws Exception {
    return parseWellForm(new StringReader(data)) ;
  }

  public Document parseWellForm(Reader reader) throws Exception {
    Document doc = wellFormParser.parse(new InputSource(reader));
    return doc ;
  }

  public Document parseWellForm(InputStream is) throws Exception {
    Document doc = wellFormParser.parse(is);
    return doc ;
  }

  public TNode toTNode(String html) throws Exception {
    Document doc = parse(html) ;
    return buildTNode(null, doc) ;
  }

  public String reformat(String html) throws IOException {
    return html ;
  }

  private TNode buildTNode(TNode parent, Node node) {
    TNode tnode = new TNode(parent) ;
    String nodeName = node.getNodeName().toLowerCase() ; 
    tnode.setNodeName(nodeName) ;
    tnode.setNodeValue(node.getNodeValue());	

    if("meta".equalsIgnoreCase(nodeName)) {
      Element element = (Element) node ;
      tnode.addAttribute("name", element.getAttribute("name")) ;
      tnode.addAttribute("http-equiv", element.getAttribute("http-equiv")) ;
      tnode.addAttribute("content", element.getAttribute("content")) ;
    }

    if("a".equalsIgnoreCase(nodeName)) {
      Element element = (Element) node ;
      tnode.addAttribute("href", element.getAttribute("href")) ;
      tnode.addAttribute("title", element.getAttribute("title")) ;
    }
    if("img".equalsIgnoreCase(nodeName)) {
      Element element = (Element) node ;
      tnode.addAttribute("src", element.getAttribute("src")) ;
      tnode.addAttribute("alt", element.getAttribute("alt")) ;
    }
    if(node instanceof Element) {
      Element ele = (Element) node ;
      tnode.setCssClass(ele.getAttribute("class")) ;
      tnode.setElementId(ele.getAttribute("id")) ;
    }

    NodeList nlist = node.getChildNodes() ;
    if(nlist.getLength() > 0) {
      for(int i = 0; i < nlist.getLength(); i++) {
        Node nchild = nlist.item(i) ;
        int  nchildType = nchild.getNodeType() ;
        if(nchildType == Node.PROCESSING_INSTRUCTION_NODE) continue ;
        if(nchildType == Node.COMMENT_NODE) continue ;
        String nchildNodeName = nchild.getNodeName() ;
        if(nchildNodeName == null) continue ;
        if("script".equalsIgnoreCase(nchildNodeName)) continue ;
        if("style".equalsIgnoreCase(nchildNodeName)) continue ;
        if("iframe".equalsIgnoreCase(nchildNodeName)) continue ;
        TNode tnodeChild = buildTNode(tnode, nchild) ;
        tnode.addChild(tnodeChild) ;
      }
    }
    return tnode; 
  }
}