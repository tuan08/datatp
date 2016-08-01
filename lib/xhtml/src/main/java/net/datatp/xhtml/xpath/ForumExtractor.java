package net.datatp.xhtml.xpath;

import java.util.List;

import net.datatp.util.text.matcher.StringExpMatchers;
import net.datatp.util.text.matcher.StringSetMatcher;

public class ForumExtractor implements XPathExtractor  {
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
  public int extract(WDataContext context) {
    int extractCount = 0;
    XPathStructure structure = context.getXpathStructure();
    List<XPath> candidates = structure.getXPathTree().select(candidateSelector, 20);
    if(candidates.size() < 5) return extractCount;
    XPath titleXPath = structure.findTitleHeaderCandidate();
    if(titleXPath != null) {
      context.save(new XPathExtract("forum:title", titleXPath));
      extractCount++;
    }
    XPath forumPostCandidate = structure.findCommonAncestorXPath(candidates);
    XPathTree forumPostXPathTree = structure.getXPathTree().subTree(forumPostCandidate);
    context.save(new XPathExtract("forum:posts", forumPostXPathTree.getXPathAsArray()));
    extractCount++ ;
    return extractCount;
  }
}
