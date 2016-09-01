package net.datatp.xhtml.extract;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.TextNode;

import net.datatp.util.text.matcher.StringExpMatchers;
import net.datatp.util.text.matcher.StringMatcher;
import net.datatp.util.text.matcher.StringSetMatcher;
import net.datatp.xhtml.xpath.XPath;
import net.datatp.xhtml.xpath.XPathStructure;
import net.datatp.xhtml.xpath.XPathTree;

public class CommentExtractor implements WDataExtractor  {
  final static String[] COMMENT_TITLE_LABELS = {
    "Ý KIẾN BẠN DỌC", "Ý kiến bạn đọc", "Các ý kiến khác cùng", 
    "Đóng góp ý kiến sản phẩm", "Thảo luận", "bình luận", "phản hồi", 
    "Ý kiến bạn đọc", "Comments"
  };

  final static String[] COMMENT_POST_LABELS = {
    "Tham gia", "Bài gửi", "Cảm ơn", "Thành viên", "Registered User", "Bài viết",
    "Được cảm ơn", "Trả lời kèm trích dẫn", "Tham gia từ", "Trạng thái", "Join Date:", "Posts:",
    "Trả lời", "Vi phạm", "Chia sẻ"
  } ;

  final static String[] COMMENT_NODE_CSS_PATTERN = {
    "*comment*", "*cm*", "*post*", "*review*", "*reply*", "*feedback*", "*item*"
  } ;
  
  private StringMatcher  commentHeaderMatcher ;
  private StringMatcher  commentLabelMatcher;
  private StringMatcher  cssMatchers;
  
  public CommentExtractor() {
    commentHeaderMatcher  = new StringSetMatcher(COMMENT_TITLE_LABELS, true);
    commentLabelMatcher   = new StringSetMatcher(COMMENT_POST_LABELS, true);
    cssMatchers           = new StringExpMatchers(COMMENT_NODE_CSS_PATTERN);
  }
  
  @Override
  public WDataExtract extract(WDataExtractContext context) {
    WDataExtract extract = new WDataExtract("comment");
    XPathTree commentBlock = findPostBlock(context.getXpathStructure());
    if(commentBlock == null) return null;
    extract.add(new XPathExtract("comments", commentBlock.getXPathAsArray()));
    return extract;
  }
  
  @Override
  public ExtractEntity extractEntity(WDataExtractContext context) {
    XPathTree commentBlock = findPostBlock(context.getXpathStructure());
    if(commentBlock == null) return null;
    ExtractEntity entity = new ExtractEntity(ExtractEntity.COMMENT, ExtractEntity.COMMENT);
    entity.withComment(commentBlock.getText());
    return entity;
  }
  
  private XPathTree findPostBlock(XPathStructure structure) {
    List<XPath> allXPath = structure.getXPathTree().getXPaths();
    for(int i = 0; i < allXPath.size(); i++) {
      XPath xpath = allXPath.get(i);
      if(xpath.getSection() != XPath.Section.Body) continue;
      if(xpath.isTextNode()) {
        String text = ((TextNode)xpath.getNode()).text();
        text = text.trim().toLowerCase();
        if(commentHeaderMatcher.matches(text)) {
          List<XPath> candidates = new ArrayList<>();
          candidates.add(xpath);
          int limit = i + 15;
          if(limit > allXPath.size()) limit = allXPath.size();
          for(int j = i + 1; j < limit; j++) {
            XPath sel = allXPath.get(j);
            if(!sel.isTextNode()) continue;
            text = ((TextNode)sel.getNode()).text();
            text = text.trim().toLowerCase();
            if(commentLabelMatcher.matches(text)) {
              candidates.add(sel);
            }
            if(candidates.size() >= 3) {
              XPath found = structure.findCommonAncestorXPath(candidates);
              return structure.getXPathTree().subTree(found);
            }
          }
        }
      }
    }
    return null;
  }
}
