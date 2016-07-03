package net.datatp.xhtml.dom.extract;

import org.junit.Test;

import net.datatp.xhtml.dom.extract.DocumentExtractor;
import net.datatp.xhtml.fetcher.Fetcher;
import net.datatp.xhtml.fetcher.HttpClientFetcher;

public class BlogContentExtractorUnitTest {
  static String[] EXPECT_BLOG_TAG_DETAIL = {"content:blog", "content:detail"} ;

  static URLVerifier WORDPRESS = new URLVerifier(
      "Làng trong phố (12) – sư ở chùa làng",
      "http://trangha.wordpress.com",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  //TODO: Don't get BODY
  static URLVerifier MINHMEO = new URLVerifier(
      "Namecheap giảm giá còn 1,99$/domain",
      "http://minhmeo.info/vinh-biet-nhe-co-cc-google.html",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  //TODO: Don't get BODY
  static URLVerifier HUTRUC = new URLVerifier(
      "Up rom Galaxy SII + Link down nhanh",
      "http://hutruc.com/667/up-rom-galaxy-sii-link-down-nhanh.html",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  static URLVerifier _123YEUCUOCSONG = new URLVerifier(
      "Cuộc sống muôn màu",
      "http://123yeu.org/Cuoc-song-muon-mau/",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  static URLVerifier YUME = new URLVerifier(
      "Thời trang cho đúng học đường",
      "http://blog.yume.vn/xem-blog/thoi-trang-cho-dung-hoc-duong.bmk2978.35D58C29.html",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  static URLVerifier YAHOOPLUS = new URLVerifier(
      "Danh sách các thí sinh vào vòng bán kết tại Đại học Quy Nhơn",
      "http://vn.360plus.yahoo.com/ngoisao_hocduong/article?new=1&mid=2547",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  static URLVerifier TAMTAY = new URLVerifier(
      "Thì ra tình yêu là định mệnh (Đá và Phật nói về tình yêu)",
      "http://blog.tamtay.vn/entry/view/710382/Thi-ra-tinh-yeu-la-dinh-menh-Da-va-Phat-noi-ve-tinh-yeu.html",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  static URLVerifier BLOGVIET = new URLVerifier(
      "Blog Radio 199: Khi tình yêu còn đỏ trong trái tim mỗi người",
      "http://blogviet.dalink.vn/blogviet/story/blogradio/24584/index.aspx",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  static URLVerifier P212PRO = new URLVerifier(
      "5 ứng dụng Chrome dành cho dân văn phòng",
      "http://www.p212pro.tk/2011/09/5-ung-dung-chrome-danh-cho-dan-van.html",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  static URLVerifier OPERA = new URLVerifier(
      "Chú ý một tí...",
      "http://my.opera.com/phamlam/blog/",
      DocumentExtractor.Type.blog, EXPECT_BLOG_TAG_DETAIL
  );
  
  private void verifyAll(Fetcher fetcher) throws Exception {
    URLVerifier[] all = {
       WORDPRESS, MINHMEO, HUTRUC, _123YEUCUOCSONG, YUME,
       YAHOOPLUS, TAMTAY, BLOGVIET, P212PRO, OPERA
    };
    for(URLVerifier sel : all) sel.verify(fetcher, false) ;
  }
  
  @Test
  public void test() throws Exception {
    Fetcher fetcher = new HttpClientFetcher();
    //verifyAll(fetcher) ;
    //WORDPRESS.verify(fetcher, true) ;
    //MINHMEO.verify(fetcher, true) ;
    //HUTRUC.verify(fetcher, true) ;
    //_123YEUCUOCSONG.verify(fetcher, true) ;
    //YUME.verify(fetcher, true) ;
    //YAHOOPLUS.verify(fetcher, true) ;
    //TAMTAY.verify(fetcher, true);
    //BLOGVIET.verify(fetcher, true);
    //P212PRO.verify(fetcher, true) ;
    OPERA.verify(fetcher, true) ;
  }
}
