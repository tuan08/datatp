package net.datatp.xhtml.extract;

import java.util.List;

import org.junit.Test ;

import net.datatp.xhtml.WData;
import net.datatp.xhtml.util.WDataHttpFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class BoilerpipeContentExtractorUnitTest {
  @Test
  public void testArticle() throws Exception {
    String anchorText = "Bão Mirinae hướng vào đồng bằng Bắc Bộ";
    String url = "http://vnexpress.net/tin-tuc/thoi-su/bao-mirinae-huong-vao-dong-bang-bac-bo-3442861.html";
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wPageData = fetcher.fetch(anchorText, url);
    WDataExtractContext context = new WDataExtractContext(wPageData);
    
    List<ExtractEntity> entities = WDataExtractor.extractEntity(context, new BoilerpipeContentExtractor("article"));
    System.out.println(ExtractEntity.toString(entities));
  }
  
  @Test
  public void testArticleList() throws Exception {
    String anchorText = "Kinh doanh";
    String url = "http://kinhdoanh.vnexpress.net/";
    WDataHttpFetcher fetcher = new WDataHttpFetcher();
    WData wPageData = fetcher.fetch(anchorText, url);
    WDataExtractContext context = new WDataExtractContext(wPageData);
    
    List<ExtractEntity> entities = WDataExtractor.extractEntity(context, new BoilerpipeContentExtractor("article"));
    System.out.println(ExtractEntity.toString(entities));
  }
}