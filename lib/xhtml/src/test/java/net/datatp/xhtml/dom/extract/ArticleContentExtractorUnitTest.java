package net.datatp.xhtml.dom.extract;

import org.junit.Test ;

import net.datatp.http.SimpleHttpFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ArticleContentExtractorUnitTest {
  static String[] EXPECT_ARTICLE_TAG_DETAIL = {"content:article", "content:detail"} ;

  static URLVerifier VNEXPRESS = new URLVerifier(
      "Việt Nam luôn sẵn sàng mọi biện pháp duy trì hòa bình ở Biển Đông",
      "http://vnexpress.net/tin-tuc/the-gioi/viet-nam-luon-san-sang-moi-bien-phap-duy-tri-hoa-binh-o-bien-dong-3436403.html",
      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
  );
  
  static URLVerifier DANTRI = new URLVerifier(  
    "Lạm thu khó “chữa”, các nhà giáo dục “hiến kế”",
    "http://dantri.com.vn/c25/s25-520551/lam-thu-kho-chua-cac-nha-giao-duc-hien-ke.htm",
    DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
  );
  
//  static URLVerifier BEENET = new URLVerifier(
//      "TG 24 giờ qua ảnh: Cưỡi ngựa dã ngoại trên lòng hồ",
//      "http://bee.net.vn/channel/1987/201109/TG-24-gio-qua-anh-Chan-tran-di-tren-than-hong-1813108/",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
//  

//  
//  static URLVerifier _24H = new URLVerifier(
//      "KD xăng dầu: Có dấu hiệu gian lận",
//      "http://hn.24h.com.vn/tin-tuc-trong-ngay/kd-xang-dau-co-dau-hieu-gian-lan-c46a405638.html",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
//  
//  static URLVerifier BONGDA = new URLVerifier(
//      "Ai sẽ giải cứu Premier League: Man City, Liverpool hay... Wolverhamton?",
//      "http://www.bongda.com.vn/Goc-Ban-Doc/173039.aspx",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
//  
//  static URLVerifier NGOISAONET = new URLVerifier(
//      "Rộ tin Châu Tấn phá thai sau khi bỏ Vương Sóc",
//      "http://ngoisao.net/tin-tuc/chau-a/2011/09/ro-tin-chau-tan-pha-thai-sau-khi-bo-vuong-soc-177828/",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
//  
//  //TODO: Fetcher: read time out
//  static URLVerifier VIETNAMNET = new URLVerifier(
//      "Microsoft lỗ 5,5 tỉ USD với công cụ tìm kiếm Bing",
//      "http://vietnamnet.vn/vn/cong-nghe-thong-tin-vien-thong/40587/microsoft-lo-5-5-ti-usd-voi-cong-cu-tim-kiem-bing.html",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
//  
//  static URLVerifier DOCBAO = new URLVerifier(
//      "Một phụ nữ trẻ đã hy sinh thân mình để che chở và bảo vệ đứa con nhỏ của cô trong trận động đất kinh hoàng ở Nhật Bản hồi tháng 3.",
//      "http://docbao.vn/News.aspx?cid=35&id=114429&d=23092011",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
//  
//  static URLVerifier XALUAN = new URLVerifier(
//      "Máy tính bảng đầu tiên của Lenovo ra mắt tại Việt Nam",
//      "http://www.xaluan.com/modules.php?name=News&file=article&sid=295869",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
//  
//  static URLVerifier VIETBAO = new URLVerifier(
//      "Giật mình công nghệ \"nhuộm màu\" cốm làng Vòng",
//      "http://vietbao.vn/Doi-song-Gia-dinh/Giat-minh-cong-nghe-nhuom-mau-com-lang-Vong/2131369808/111/",
//      DocumentExtractor.Type.article, EXPECT_ARTICLE_TAG_DETAIL
//  );
  
  private void verifyAll(SimpleHttpFetcher fetcher) throws Exception {
    URLVerifier[] all = {
       VNEXPRESS, DANTRI, 
       //_24H, BEENET, NGOISAONET, BONGDA, VIETNAMNET, DOCBAO, XALUAN, VIETBAO
    };
    for(URLVerifier sel : all) sel.verify(fetcher, true /*dump*/) ;
  }
  
  @Test
  public void test() throws Exception {
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    verifyAll(fetcher) ;
  }
}