package net.datatp.xhtml.xpath;

import net.datatp.util.text.AnyStringMatcher;

public class CommentExtractor implements XpathExtractor  {
  final static String[] COMMENT_LABELS = {
    "Ý KIẾN BẠN DỌC", "Ý kiến bạn đọc", "Các ý kiến khác cùng", 
    "Đóng góp ý kiến sản phẩm", "Thảo luận", "bình luận", "phản hồi", 
    "Ý kiến bạn đọc", "Comments"
  };

  final static String[] POST_LABELS = {
    "Tham gia", "Bài gửi", "Cảm ơn", "Thành viên", "Registered User", "Bài viết",
    "Được cảm ơn", "Trả lời kèm trích dẫn", "Tham gia từ", "Trạng thái", "Join Date:", "Posts:"
  } ;

  final static String[] COMMENT_CSS_PATTERN = {
    "*comment*", "*cm*", "*post*", "*review*", "*reply*", "*feedback*", "*item*"
  } ;
  
  public AnyStringMatcher commentLabelMatcher ;
  
  public CommentExtractor(String[] commentLabels) {
    commentLabelMatcher = new AnyStringMatcher(commentLabels, true);
  }
  
  @Override
  public XPathExtract[] extract(XPathStructure structure) {
    return null;
  }
}
