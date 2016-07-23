package net.datatp.xhtml.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

public class XPathStructure {
  private String                   url;
  private String                   anchorText;
  private Document                 document;
  private XPathTree                xpathTree;
  private XPathRepetions           xpathRepetions;

  private Set<String>              taggedByTaggers = new HashSet<>();
  private Map<String, CacheSelect> cacheSelects    = new HashMap<>();

  public XPathStructure(Document doc) {
    document = doc;
  }
  
  public XPathStructure(String url, String anchorText, Document doc) {
    this.url        = url;
    this.anchorText = anchorText;
    this.document   = doc;
  }
  
  public String getUrl() { return this.url; }
  
  public String getAnchorText() { return this.anchorText ; }
  
  public Document getDocument() { return document; }
  
  public XPathTree getXPathTree() {
    if(xpathTree == null) xpathTree = new XPathCollector().process(this);
    return xpathTree;
  }
  
  public XPath getXPath(String xpath) { 
    if(xpathTree == null) xpathTree = new XPathCollector().process(this);
    return xpathTree.getXPath(xpath); 
  }
  
  public XPath getXPath(Node node) {
    if(xpathTree == null) xpathTree = new XPathCollector().process(this);
    XPath xpath = xpathTree.getXPath(node.attr(XPathTagger.XPATH_ATTR)); 
    return xpath;
  }
  
  public XPath findCommonAncestorXPath(XPath ...  xpath) {
    if(xpath == null || xpath.length == 0) return null;
    XPath commonAncestor = xpath[0];
    for(int i = 1; i < xpath.length; i++) {
      String commonAncestorXPath = commonAncestor.findClosestAncestor(xpath[i]);
      commonAncestor = getXPathTree().getXPath(commonAncestorXPath);
    }
    return commonAncestor;
  }
  
  public XPath findClosestAncestor(XPath ... xpath) {
    int limit = Integer.MAX_VALUE;
    for(int i = 0; i < xpath.length; i++) {
      if(xpath[i].fragment.length < limit) limit = xpath[i].fragment.length;
    }
    XPath.Fragment[] fragment = xpath[0].fragment;
    StringBuilder b = new StringBuilder();
    for(int i = 0; i < limit; i++) {
      boolean equals = true;
      for(int j = 1; j < xpath.length; j++) {
        if(!fragment[i].name.equals(xpath[j].fragment[i].name) ||
            fragment[i].index != xpath[j].fragment[i].index) {
          equals = false;
          break;
        }
          
      }
      if(!equals) break;
      if(b.length() > 0) b.append("/");
      b.append(fragment[i].name).append('[').append(fragment[i].index).append(']');
    }
    XPath commonAncestor = getXPathTree().getXPath(b.toString());
    return commonAncestor;
  }
  
  public XPathRepetions getXPathRepetions() {
    if(xpathRepetions != null) return xpathRepetions;
    XPathRepetionAnalyzer[] analyzer = {
      XPathRepetionAnalyzer.LINK_ANALYZER, XPathRepetionAnalyzer.TEXT_ANALYZER
    };
    xpathRepetions = new XPathRepetions();
    for(XPath xpath : getXPathTree().getFlatXPaths()) {
      xpathRepetions.add(xpath);
    }
    xpathRepetions.analyze(this, analyzer);
    return xpathRepetions;
  }
  
  public XPathStructure buildTag(NodeTagger ... taggers) {
    for(NodeTagger sel : taggers) {
      if(taggedByTaggers.contains(sel.getName())) continue;
      document.traverse(sel);
      taggedByTaggers.add(sel.getName());
    }
    return this;
  }
  
  public String findTitle() { return findFirstElementText("html > head > title"); }
  
  public String findBase()  {
    Element baseEle = findFirstElement("html > head > base");
    if(baseEle == null) return null ;
    return baseEle.attr("href") ;
  }

  public List<XPath> findAllLinks() { 
    Elements elements = select("a[href]");
    List<XPath> holder = new ArrayList<>();
    for(int i = 0; i < elements.size(); i++) {
      holder.add(getXPath(elements.get(i)));
    }
    return holder; 
  }
  
  
  public Element findFirstElement(String query) { return select(query).first(); }
  
  public String findFirstElementText(String query) {
    Element element = select(query).first();
    if(element == null) return null;
    return element.text();
  }
  
  public Elements select(String query) {
    CacheSelect cache = cacheSelects.get(query) ;
    if(cache != null) return cache.getElements();
    Elements elements = document.select(query);
    cache = new CacheSelect(query, elements);
    cacheSelects.put(query, cache);
    return elements;
  }
  
  
  
  static public class CacheSelect {
    private String   query;
    private Elements elements;
    
    public CacheSelect(String query, Elements elements) {
      this.query    = query;
      this.elements = elements;
    }
    
    public String getQuery() { return this.query ; }
    
    public Elements getElements() { return this.elements; }
  }
  
  static public class XPathCollector implements NodeVisitor {
    private TreeMap<String, XPath> xpaths ;

    public XPathTree process(XPathStructure doc) {
      xpaths = new TreeMap<>();
      doc.buildTag(new XPathTagger());
      doc.getDocument().traverse(this);
      return new XPathTree(xpaths);
    }
    
    @Override
    public void head(Node node, int depth) {
      String xpath = node.attr(XPathTagger.XPATH_ATTR);
      xpaths.put(xpath, new XPath(xpath, node));
    }
    
    @Override
    public void tail(Node node, int depth) { }
  }
}
