package net.datatp.xhtml.dom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.datatp.util.text.StringExpMatcher;
import net.datatp.util.text.StringUtil;
import net.datatp.xhtml.dom.selector.Selector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TNode {
  static String[] BLOCK_TAG = {
    "h1", "h2", "h3", "h4", "h5", "h6", "p", "tr", "table", "li", "ul", "div"
  };

  static String[] INLINE_TAG = {
      "a", "span", "em", "strong", "i", "b", "font", 
  };

  private TNode      parent   ;
  private String     nodeName ;
  private String     value ;
  private TNodeXPath xpath  ;
  private String     cssClass ;
  private String     elementId ;

  private HashSet<String> tags ;
  private Map<String, String> attributes ;
  private List<TNode> children ;

  public TNode(TNode parent) {
    this.parent = parent ;
  }

  public TNode(TNode parent, TNode node) {
    this.parent = parent ;
    this.nodeName = node.getNodeName() ; 
    this.value = node.getNodeValue();
    this.xpath = node.getXPath() ;
    this.cssClass  = node.cssClass ;
    this.elementId = node.elementId ;

    if(node.attributes != null) 
      attributes = new HashMap<String, String>(node.attributes) ;
    if(node.tags != null) 
      this.tags = new HashSet<String>(node.tags) ;

    if(node.children != null) {
      children = new ArrayList<TNode>() ;
      for(int i = 0; i < node.children.size(); i++) {
        children.add(new TNode(this, node.children.get(i))) ;
      }
    }
  }

  public TNode getParent() { return this.parent ; }

  public TNode getAncestor(int level) { 
    if(level < 1) return this ;
    TNode ret = parent ;
    int backLevel = 1 ;
    while(ret != null && backLevel <= level) {
      if(backLevel == level) return ret ;
      ret = ret.getParent() ;
      backLevel++ ;
    }
    return null ; 
  }

  public TNode getAncestor(TNodeXPath xpath) {
    int level = this.xpath.getDeep() - xpath.getDeep() ;
    TNode node = getAncestor(level);
    if(node.getXPath().equals(xpath)) return node; 
    return null ;
  }

  public TNode getAncestorByNodeName(String name) { 
    TNode ret = parent ;
    while(ret != null) {
      if(name.equals(ret.getNodeName())) return ret ;
      ret = ret.getParent() ;
    }
    return null ; 
  }

  public TNode getAncestorByNodeName(String[] name) { 
    TNode ret = parent ;
    while(ret != null) {
      if(StringUtil.isIn(ret.getNodeName(), name)) return ret ;
      ret = ret.getParent() ;
    }
    return null ; 
  }

  public boolean hasAncestor(TNode node) { 
    TNode ancestor = parent ;
    while(ancestor != null) {
      if(ancestor == node) return true ;
      ancestor = ancestor.getParent() ;
    }
    return false ; 
  }

  public boolean hasAncestorTag(String ... tag) { 
    TNode ancestor = this ;
    while(ancestor != null) {
      if(ancestor.hasTag(tag)) return true ;
      ancestor = ancestor.getParent() ;
    }
    return false ; 
  }

  public TNode append(StringBuilder b) {
    if(this.value != null) {
      b.append(this.value) ;
      if(StringUtil.isIn(parent.nodeName, INLINE_TAG)) {
        b.append(" ") ;
      } else {
        b.append("\n") ;
      }
    } else {
      b.append(this.getTextContent()) ;
      if(StringUtil.isIn(nodeName, INLINE_TAG)) {
        b.append(" ") ;
      } else {
        b.append("\n") ;
      }
    }
    return this ;
  }

  public String getNodeName() { return nodeName;}
  public void   setNodeName(String nodeName) { 
    this.nodeName = nodeName.toLowerCase() ; 
  }

  public String getNodeValue() { return value; }
  public void   setNodeValue(String text) { 
    this.value = normalize(text);	
    if(value != null) {
      value = value.trim() ;
      if(value.length() == 0) value = null ;
    }
  }

  public String getCssClass() { return this.cssClass ; }
  public void   setCssClass(String cssClass) {
    if(cssClass == null || cssClass.length() == 0) this.cssClass = null ;
    else this.cssClass = cssClass.toLowerCase() ; 
  }

  public String getElementId() { return this.elementId ; }
  public void   setElementId(String id) {
    if(id == null || id.length() == 0) this.elementId = null ;
    else this.elementId = id.toLowerCase() ; 
  }

  public TNodeXPath getXPath() { 
    if(xpath == null) xpath = new TNodeXPath(this) ;
    return xpath ; 
  }

  public void remove(TNode child) {
    if(children == null) return ;
    Iterator<TNode> i = children.iterator() ;
    while(i.hasNext()) {
      if(child == i.next()) {
        i.remove();
        break ;
      }
    }
  }

  public void visit(TNodeVisitor visitor) {
    int code = visitor.onVisit(this) ;
    if(code == TNodeVisitor.SKIP) return ;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        children.get(i).visit(visitor) ;
      }
    }
  }

  public TNode[] select(final Selector selector) {
    final List<TNode> holder = new ArrayList<TNode>() ;
    TNodeVisitor visitor = new TNodeVisitor() {
      public int onVisit(TNode node) {
        if(selector.isSelected(node)) {
          holder.add(node) ;
          return SKIP;
        }
        return CONTINUE ;
      }
    };
    visit(visitor) ;
    return holder.toArray(new TNode[holder.size()]) ;
  }

  public TNode[] select(final Selector selector, final boolean ignoreSelectChildren) {
    final List<TNode> holder = new ArrayList<TNode>() ;
    TNodeVisitor visitor = new TNodeVisitor() {
      public int onVisit(TNode node) {
        if(selector.isSelected(node)) {
          holder.add(node) ;
          if(ignoreSelectChildren) return SKIP;
        }
        return CONTINUE ;
      }
    };
    visit(visitor) ;
    return holder.toArray(new TNode[holder.size()]) ;
  }

  public void addTag(String tag) {
    if(tags == null) tags = new HashSet<String>() ;
    tags.add(tag) ;
  }

  public boolean hasTag(String tag) {
    if(tags == null) return false ;
    return tags.contains(tag) ;
  }

  public boolean hasTag(String[] tag) {
    if(tags == null) return false ;
    for(String sel : tag) {
      if(tags.contains(sel)) return true ;
    }
    return false ;
  }

  public boolean hasDescendant(TNode node) {
    if(node == this) return true;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        if(children.get(i).hasDescendant(node)) return true ;
      }
    }
    return false ;
  }

  public boolean hasDescendantTag(String tag) {
    if(hasTag(tag)) return true;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        if(children.get(i).hasDescendantTag(tag)) return true ;
      }
    }
    return false ;
  }

  public boolean hasDescendantTag(String[] tag) {
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        TNode child = children.get(i) ;
        if(child.hasTag(tag)) return true;
        if(child.hasDescendantTag(tag)) return true ;
      }
    }
    return false ;
  }

  public HashSet<String> getTags() { return tags; }
  public void            setTags(HashSet<String> tags) { this.tags = tags; }

  public void addAttribute(String name, String value) {
    if(value == null || value.length() == 0) return ;
    if(attributes == null) attributes = new HashMap<String, String>() ;
    attributes.put(name, value) ;
  }
  public String getAttribute(String name) {
    if(attributes == null) return null ;
    return attributes.get(name) ;
  }
  public Map<String, String> getAttributes() { return attributes; }
  public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }

  public TNode findDescendantByXPath(TNodeXPath xpath) {
    if(this.xpath.equals(xpath)) return this ;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        TNode found = children.get(i).findDescendantByXPath(xpath) ;
        if(found != null) return found ;
      }
    }
    return null ;
  }

  public List<TNode> findDescendantByElementId(String matcher) {
    return findDescendantByElementId(new StringExpMatcher(matcher.toLowerCase()));
  }

  public List<TNode> findDescendantByElementId(StringExpMatcher matcher) {
    List<TNode> holder = new ArrayList<TNode>() ;
    findDescendantByElementId(holder, matcher) ;
    return holder ;
  }

  void findDescendantByElementId(List<TNode> holder, StringExpMatcher matcher) {
    if(matcher.matches(this.elementId)) {
      holder.add(this) ;
    }
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        children.get(i).findDescendantByElementId(holder, matcher) ;
      }
    }
  }


  public TNode findFirstDescendantByElementId(String matcher) {
    return findFirstDescendantByElementId(new StringExpMatcher(matcher.toLowerCase())) ;
  }

  public TNode findFirstDescendantByElementId(StringExpMatcher matcher) {
    if(matcher.matches(this.elementId)) return this ;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        TNode child = children.get(i) ;
        TNode ret = child.findFirstDescendantByElementId(matcher) ;
        if(ret != null) return ret ;
      }
    }
    return null ;
  }

  public List<TNode> findDescendantByCssClass(String matcher) {
    List<TNode> holder = new ArrayList<TNode>() ;
    findDescendantByCssClass(holder, new StringExpMatcher(matcher.toLowerCase())) ;
    return holder ;
  }

  public List<TNode> findDescendantByCssClass(StringExpMatcher matcher) {
    List<TNode> holder = new ArrayList<TNode>() ;
    findDescendantByCssClass(holder, matcher) ;
    return holder ;
  }

  void findDescendantByCssClass(List<TNode> holder,StringExpMatcher matcher) {
    if(matcher.matches(this.cssClass)) holder.add(this) ;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        TNode child = children.get(i) ;
        child.findDescendantByCssClass(holder, matcher) ;
      }
    }
  }

  public TNode findFirstDescendantByCssClass(String matcher) {
    return findFirstDescendantByCssClass(new StringExpMatcher(matcher.toLowerCase())) ;
  }

  public TNode findFirstDescendantByCssClass(StringExpMatcher matcher) {
    if(matcher.matches(this.cssClass)) return this ;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        TNode child = children.get(i) ;
        TNode ret = child.findFirstDescendantByCssClass(matcher) ;
        if(ret != null) return ret ;
      }
    }
    return null ;
  }

  public void addChild(TNode anode) {
    if(this.children == null) children = new ArrayList<TNode>() ;
    children.add(anode) ;
  }
  public List<TNode> getChildren() { return children; }
  public void setChildren(List<TNode> children) { this.children = children; }

  public int getTextSize() {
    int sum = 0;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        sum +=  children.get(i).getTextSize() ;
      }
    }
    if(value != null) sum += value.length() ;
    return sum ;
  }

  public int getTextNodeCount() {
    int sum = 0;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        sum +=  children.get(i).getTextNodeCount() ;
      }
    }
    if(value != null) sum += 1 ;
    return sum ;
  }

  public int getLinkTextSize() {
    if("a".equalsIgnoreCase(nodeName)) return getTextSize() ;
    int sum = 0;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        sum +=  children.get(i).getLinkTextSize() ;
      }
    }
    return sum ;
  }

  public int getLinkNodeCount() {
    if("a".equalsIgnoreCase(nodeName)) return 1 ;
    int sum = 0;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        sum +=  children.get(i).getLinkNodeCount() ;
      }
    }
    return sum ;
  }

  public int getLinkTextNodeCount() {
    if("a".equalsIgnoreCase(nodeName)) {
      if(children != null) {
        for(int i = 0; i < children.size(); i++) {
          if(children.get(i).getNodeValue() != null) return 1 ;
        }
      }
      return 0 ;
    }
    int sum = 0;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        sum +=  children.get(i).getLinkTextNodeCount() ;
      }
    }
    return sum ;
  }

  public float getLinkTextDensity() {
    int linkTextSize = getLinkTextSize() ;
    int textSize = getTextSize() ;
    if(textSize == 0) return 1f ;
    return linkTextSize/(float)textSize ;
  }

  public float getLinkNodeDensity() {
    int linkNode = getLinkNodeCount() ;
    int textNodeCount = getTextNodeCount() ;
    if(textNodeCount == 0) return 0f ;
    return linkNode/(float)textNodeCount ;
  }

  public boolean isEmpty() {
    if(value == null || value.length() == 0) {
      if(children == null || children.size() == 0) {
        if(attributes == null) return true; 
      } else {
        for(int i = 0; i < children.size(); i++) {
          TNode child =  children.get(i) ;
          if(!child.isEmpty()) return false ;
        }
        if(attributes == null) return true;
      }
    }
    return false  ;
  }

  public boolean isEmptyText() {
    if(value == null || value.length() == 0) {
      if(children == null || children.size() == 0) {
        return true; 
      } else {
        for(int i = 0; i < children.size(); i++) {
          TNode child =  children.get(i) ;
          if(!child.isEmptyText()) return false ;
        }
      }
    }
    return false  ;
  }

  public String getTextContent() {
    StringBuilder b = new StringBuilder() ;
    appendNodeValue(b) ;
    return b.toString() ;
  }

  void collectTextFragments(List<String> holder ) {
    if(value != null) holder.add(value) ;
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        children.get(i).collectTextFragments(holder) ;
      }
    }
  }

  public boolean hasDescendantNodeName(String[] name) { 
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        TNode child = children.get(i) ;
        if(StringUtil.isIn(this.nodeName, name)) return true ;
        if(child.hasDescendantNodeName(name))    return true; 
      }
    }
    return false ;
  }

  protected void appendNodeValue(StringBuilder b) {
    if(value != null) {
      b.append(value) ;
      if(StringUtil.isIn(parent.nodeName, INLINE_TAG)) {
        b.append(" ") ;
      } else {
        b.append("\n") ;
      }
    }
    if(children != null) {
      for(int i = 0; i < children.size(); i++) {
        children.get(i).appendNodeValue(b) ;
      }
    }
  }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("tag = ").append(StringUtil.join(getTags(), ",")) ;
    b.append(" | ") ;
    b.append("cssclass = ").append(getCssClass()) ;
    b.append(" | ") ;
    b.append("xpathNode = ").append(getXPath()) ;
    b.append("\n") ;
    b.append(getTextContent()) ;
    return b.toString() ;
  }

  private String normalize(String s) {
    if(s == null || s.length() == 0) return s ;
    StringBuilder b = new StringBuilder() ;
    char[] buf = s.toCharArray() ;
    for(char sel : buf) {
      if(sel == 160) b.append(' ') ;
      else b.append(sel) ;
    }
    return b.toString() ;
  }
}