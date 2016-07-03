package net.datatp.xhtml.dom.extract;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.TNodeGroup;
import net.datatp.xhtml.dom.selector.CssClassSelector;
import net.datatp.xhtml.dom.selector.OrSelector;
import net.datatp.xhtml.dom.selector.Selector;
import net.datatp.xhtml.dom.selector.TextLengthSelector;
import net.datatp.xhtml.dom.selector.TextSimilaritySelector;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CommentExtractor {
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

  private Selector commentLabelSelector = new TextSimilaritySelector(COMMENT_LABELS) ;
  private Selector commentPostSelector  ;

  public CommentExtractor() {
    Selector labelPostSelector = new TextSimilaritySelector(POST_LABELS) ;
    Selector cssPostSelector   = new CssClassSelector(COMMENT_CSS_PATTERN) ;
    commentPostSelector = new OrSelector(labelPostSelector, cssPostSelector) ;
  }

  public ExtractBlock extract(TDocument tdoc, String title) {
    TNode[] labelCandidates = tdoc.getRoot().select(commentLabelSelector, true) ;
    if(labelCandidates.length < 1) return null ;
    for(int i = 0; i < labelCandidates.length; i++) {
      ExtractBlock block = extract(title, labelCandidates[i].getParent()) ;
      if(block != null) return block ;
    }
    return null ;
  }

  public ExtractBlock extract(String title, TNode candidate) {
    int backLevel = 0;
    while(candidate != null && backLevel < 3) {
      ExtractBlock block = extractBlock(title, candidate) ;
      if(block != null) return block ;
      candidate = candidate.getAncestorByNodeName("div") ;
      backLevel++ ;
    }
    return null ;
  }

  public ExtractBlock extractBlock(String title, TNode candidate) {
    TNode[] nodes = candidate.select(commentPostSelector, true) ;
    if(nodes.length > 1) {
      TNodeGroup[] group = TNodeGroup.groupByCommonAncestor(nodes, 3) ;
      if(group.length > 0 && group[0].getTNodes().size() > 1) {
        return createComment(title, candidate) ;
      }
    }
    return null ;
  }

  protected ExtractBlock createComment(String title, TNode commentBlock) {
    StringBuilder contentB = new StringBuilder() ;
    TNode[] nodes = commentBlock.select(new TextLengthSelector(0, 100000)) ;
    for(int i = 0; i < nodes.length; i++) {
      TNode node = nodes[i] ;
      TNode[] textNode = node.select(new TextLengthSelector(0, 100000)) ;
      for(int j = 0; j < textNode.length; j++) {
        contentB.append(textNode[j].getTextContent()).append("\n") ;
      }
    }
    ExtractBlock block = new ExtractBlock("comment") ;
    block.setTitle(title, null) ;
    block.setContent(contentB.toString(), null);
    block.setTags("content:comment") ;
    return block ;
  }
}