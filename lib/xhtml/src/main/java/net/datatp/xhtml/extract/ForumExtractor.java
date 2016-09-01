package net.datatp.xhtml.extract;

import java.util.List;

import net.datatp.util.text.matcher.StringExpMatchers;
import net.datatp.util.text.matcher.StringSetMatcher;
import net.datatp.xhtml.xpath.XPath;
import net.datatp.xhtml.xpath.XPathSelector;
import net.datatp.xhtml.xpath.XPathStructure;
import net.datatp.xhtml.xpath.XPathTree;

public class ForumExtractor implements WDataExtractor  {
  final static public String[] KEYWORD_LABELS = {
    "Tham gia", "Bài gửi", "Cảm ơn", "Thành viên", "Registered User", "Bài viết",
    "Được cảm ơn", "Trả lời kèm trích dẫn", "Tham gia từ", "Trạng thái", "Join Date:", "Posts:"
  } ;

  final static String[] ELEMENT_PATTERN = { "*postbody*", "*postcontent*", "*bbCode*", "*avatar*" } ;

  final static String[] LINK_PATTERN = { "*newreply*", "*addpost*" } ;
  
  private XPathSelector labelSelector = new XPathSelector.TextNodeSelector(new StringSetMatcher(KEYWORD_LABELS));
  private XPathSelector linkSelector  = new XPathSelector.LinkNodeSelector(new StringExpMatchers(LINK_PATTERN));
  private XPathSelector cssSelector  = new XPathSelector.AttrNodeSelector("class", new StringExpMatchers(ELEMENT_PATTERN));
  
  private XPathSelector[] candidateSelector ;
  
  public ForumExtractor() {
    candidateSelector = new XPathSelector[] {
      labelSelector, linkSelector, cssSelector
    };
  }
  
  @Override
  public WDataExtract extract(WDataExtractContext context) {
    XPathStructure structure = context.getXpathStructure();
    List<XPath> candidates = structure.getXPathTree().select(candidateSelector, 20);
    if(candidates.size() < 5) return null;
    WDataExtract extract = new WDataExtract("forum");
    XPath titleXPath = structure.findTitleHeaderCandidate();
    if(titleXPath != null) extract.add(new XPathExtract("title", titleXPath));
    XPath forumPostCandidate = structure.findCommonAncestorXPath(candidates);
    XPathTree forumPostXPathTree = structure.getXPathTree().subTree(forumPostCandidate);
    extract.add(new XPathExtract("posts", forumPostXPathTree.getXPathAsArray()));
    return extract;
  }
  
  public ExtractEntity extractEntity(WDataExtractContext context) {
    XPathStructure structure = context.getXpathStructure();
    List<XPath> candidates = structure.getXPathTree().select(candidateSelector, 20);
    if(candidates.size() < 5) return null;
    
    ExtractEntity entity = new ExtractEntity("forum", "post");
    XPath titleXPath = structure.findTitleHeaderCandidate();
    if(titleXPath != null) entity.withTitle(titleXPath.getText());
    XPath forumPostCandidate = structure.findCommonAncestorXPath(candidates);
    XPathTree forumPostXPathTree = structure.getXPathTree().subTree(forumPostCandidate);
    entity.withContent(forumPostXPathTree.getText());
    return entity;
  }
}
