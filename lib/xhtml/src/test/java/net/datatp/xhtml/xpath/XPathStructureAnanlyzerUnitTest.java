package net.datatp.xhtml.xpath;

import java.util.List;

import org.junit.Test ;

import net.datatp.xhtml.WData;
import net.datatp.xhtml.extract.CommentExtractor;
import net.datatp.xhtml.extract.ForumExtractor;
import net.datatp.xhtml.extract.MainContentExtractor;
import net.datatp.xhtml.extract.WDataExtract;
import net.datatp.xhtml.extract.WDataExtractContext;
import net.datatp.xhtml.extract.WDataExtractors;
import net.datatp.xhtml.util.WDataHttpFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class XPathStructureAnanlyzerUnitTest {
  @Test
  public void testArticle() throws Exception {
    String anchorText = "Bão Mirinae hướng vào đồng bằng Bắc Bộ";
    String url = "http://vnexpress.net/tin-tuc/thoi-su/bao-mirinae-huong-vao-dong-bang-bac-bo-3442861.html";
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wPageData = fetcher.fetch(anchorText, url);
    WDataExtractContext context = new WDataExtractContext(wPageData);
    
    WDataExtractors extractor = new WDataExtractors("content", new MainContentExtractor(), new CommentExtractor());
    List<WDataExtract> extracts = extractor.extract(context);
    System.out.println(WDataExtract.format(extracts));
  }
  
  @Test
  public void testForum() throws Exception {
    String anchorText = "Thay bỏ thớt gỗ quá date mà còn bị vợ càu nhàu";
    String url = "http://www.webtretho.com/forum/f4519/thay-bo-thot-go-qua-date-ma-con-bi-vo-cau-nhau-2273789/";
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wPageData = fetcher.fetch(anchorText, url);
    WDataExtractContext context = new WDataExtractContext(wPageData);
    
    WDataExtractors extractor = new WDataExtractors("content", new ForumExtractor());
    List<WDataExtract> extracts = extractor.extract(context);
    System.out.println(WDataExtract.format(extracts));
  }
}