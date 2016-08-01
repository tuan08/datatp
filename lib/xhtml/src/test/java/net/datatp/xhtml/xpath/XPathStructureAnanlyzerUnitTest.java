package net.datatp.xhtml.xpath;

import org.junit.Test ;

import net.datatp.xhtml.SimpleHttpFetcher;
import net.datatp.xhtml.WData;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class XPathStructureAnanlyzerUnitTest {
  @Test
  public void testArticle() throws Exception {
    String anchorText = "Bão Mirinae hướng vào đồng bằng Bắc Bộ";
    String url = "http://vnexpress.net/tin-tuc/thoi-su/bao-mirinae-huong-vao-dong-bang-bac-bo-3442861.html";
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    WData wPageData = fetcher.fetch(anchorText, url);
    WDataContext context = new WDataContext(wPageData);
    
    WDataExtractor extractor = new WDataExtractor("content", new MainContentExtractor(), new CommentExtractor());
    extractor.extract(context);
    System.out.println(XPathExtract.getFormattedText(context.getXPathExtract()));
  }
  
  @Test
  public void testForum() throws Exception {
    String anchorText = "Thay bỏ thớt gỗ quá date mà còn bị vợ càu nhàu";
    String url = "http://www.webtretho.com/forum/f4519/thay-bo-thot-go-qua-date-ma-con-bi-vo-cau-nhau-2273789/";
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    WData wPageData = fetcher.fetch(anchorText, url);
    WDataContext context = new WDataContext(wPageData);
    
    WDataExtractor extractor = new WDataExtractor("content", new ForumExtractor());
    extractor.extract(context);
    System.out.println(XPathExtract.getFormattedText(context.getXPathExtract()));
  }
}