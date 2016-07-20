package net.datatp.xhtml.dom.extract;

import java.util.HashMap;
import java.util.Map;

import net.datatp.xhtml.dom.TDocument;
import net.datatp.xhtml.dom.TNode;
import net.datatp.xhtml.dom.tagger.JunkTextBlockTagger;
import net.datatp.xhtml.dom.tagger.LinkBlockTagger;
import net.datatp.xhtml.dom.tagger.Tagger;
import net.datatp.xhtml.dom.tagger.TitleBlockTagger;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentExtractor {
  final static public String[] CLASSIFIED_KEYWORD = {
    "Giá:", "giá bán", "price", "Địa chỉ", "mobile", "Đăng lúc", "Khu vực", "Ngày đăng", "Người đăng tin", 
    "Người đăng", "Mã Tin", "Khu vực", "Đăng tại", "Liên hệ qua email", "Di động"
  } ;

  final static public String[] PRODUCT_KEYWORD = {
    "giá", "giá hãng", "giá bán", "giá online", "price", "Giá niêm yết", "Khuyến mại", "Bảo hành", "Kho", 
    "Số lượng", "Mô tả", "Mã sản phẩm", "Mã hàng", "Dòng sản phẩm", "Màu sản phẩm", "Chi tiết", "(Đã bao gồm VAT)", 
    "Đang còn hàng", "Áp dụng tại"
  } ;

  final static public String[] JOB_KEYWORD = {
    "Bằng cấp", "Kinh nghiệm", "Vị trí", "Cấp bậc","Yêu cầu trình độ", "Loại hình công việc", "Ngành nghề", 
    "Mô tả công việc", "Chức danh cần tuyển", "Tên công ty", "Địa điểm làm việc", "Nơi làm việc", "Mức lương", "Hạn nộp hồ sơ", 
    "Ngày đăng"
  } ;

  static public enum Type { article, blog, forum, product, classified , job, other, ignore } 

  private Map<Type, ContentExtractor> extractors = new HashMap<Type, ContentExtractor>() ;
  private MainContentExtractor defaultExtractor  = new MainContentExtractor("content:other") ;

  private Tagger titleTagger     = new TitleBlockTagger() ;
  private Tagger linkTagger      = new LinkBlockTagger() ;
  private Tagger junkTextTagger  = new JunkTextBlockTagger() ;
  
  private CommentExtractor commentExtractor = new CommentExtractor() ;

  public DocumentExtractor() {
    extractors.put(Type.article,    new MainContentExtractor("content:article")) ;
    extractors.put(Type.blog,       new MainContentExtractor("content:blog")) ;
    extractors.put(Type.forum,      new ForumContentExtractor("content:forum")) ;
    extractors.put(Type.classified, new KeywordTextContentExtractor("content:classified", CLASSIFIED_KEYWORD)) ;
    extractors.put(Type.product,    new KeywordTextContentExtractor("content:product", PRODUCT_KEYWORD)) ;
    extractors.put(Type.job,        new KeywordTextContentExtractor("content:job", JOB_KEYWORD)) ;
    extractors.put(Type.other,      new MainContentExtractor("content:other")) ;
    extractors.put(Type.ignore,     new MainContentExtractor("content:ignore")) ;
  }

  public ExtractContent extract(Type type, TDocument tdoc) {
    TNode root = tdoc.getRoot() ;
    titleTagger.tag(tdoc, root) ;
    linkTagger.tag(tdoc, root) ;
    junkTextTagger.tag(tdoc, root) ;

    ContentExtractor extractor = extractors.get(type) ;
    ExtractContent econtent = null ;
    if(extractor == null) econtent = defaultExtractor.extract(tdoc) ;
    else econtent = extractor.extract(tdoc) ;
    String title = tdoc.getAnchorText() ;
    if(title == null) title = "Comment" ;
    ExtractBlock commentBlock = commentExtractor.extract(tdoc, title) ;
    econtent.add(commentBlock) ;
    return econtent ;
  }
}