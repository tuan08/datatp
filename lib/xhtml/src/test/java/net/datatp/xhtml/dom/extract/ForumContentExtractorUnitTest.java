package net.datatp.xhtml.dom.extract;

import org.junit.Test;

import net.datatp.xhtml.dom.extract.DocumentExtractor;
import net.datatp.xhtml.fetcher.Fetcher;
import net.datatp.xhtml.fetcher.HttpClientFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ForumContentExtractorUnitTest {
  
  static String[] EXPECT_FORUM_TAG_DETAIL = {"content:forum", "content:detail"} ;

  static URLVerifier DIENDANTINHOC = new URLVerifier(
      "Thiệt bị được gửi đến tin học xanh",
      "http://www.ddth.com/showthread.php/255479-Thi%E1%BA%BFt-b%E1%BB%8B-%C4%91%C6%B0%E1%BB%A3c-g%E1%BB%ADi-%C4%91%E1%BA%BFn-Tin-H%E1%BB%8Dc-Xanh",
      DocumentExtractor.Type.forum, EXPECT_FORUM_TAG_DETAIL
  );
  
  static URLVerifier VNZOOM = new URLVerifier(
      "Đầy đủ cú pháp và ví dụ của các hàm trong Excel",
      "http://www.vn-zoom.com/f58/day-du-cu-phap-va-vi-du-cua-cac-ham-trong-excel-83867.html",
      DocumentExtractor.Type.forum, EXPECT_FORUM_TAG_DETAIL
  );
  
  static URLVerifier TRUONGTON = new URLVerifier(
      "Những mánh lừa tân sinh viên dễ 'sập bẫy'",
      "http://truongton.net/forum/showthread.php?t=1534120",
      DocumentExtractor.Type.forum, EXPECT_FORUM_TAG_DETAIL
  );
  
  static URLVerifier WEBTRETHO = new URLVerifier(
      "Thông tin ngày Hội Thể Thao Huggies Siêu Nhí",
      "http://www.webtretho.com/forum/f2308/thong-tin-ngay-hoi-the-thao-huggies-sieu-nhi-909284/",
      DocumentExtractor.Type.forum, EXPECT_FORUM_TAG_DETAIL
  );
  
  private void verifyAll(Fetcher fetcher) throws Exception {
    URLVerifier[] all = {
       DIENDANTINHOC, TRUONGTON, VNZOOM, WEBTRETHO
    };
    for(URLVerifier sel : all) sel.verify(fetcher, false) ;
  }
  
  @Test
  public void test() throws Exception {
    Fetcher fetcher = new HttpClientFetcher();
    //verifyAll(fetcher) ;
    //DIENDANTINHOC.verify(fetcher, true) ;
    //VNZOOM.verify(fetcher, true) ;
    //TRUONGTON.verify(fetcher, true) ;
    WEBTRETHO.verify(fetcher, true) ;
  }
}