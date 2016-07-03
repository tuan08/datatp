package net.datatp.xhtml.dom.extract;

import org.junit.Test ;

import net.datatp.xhtml.dom.extract.DocumentExtractor;
import net.datatp.xhtml.dom.extract.DocumentExtractor.Type;
import net.datatp.xhtml.fetcher.Fetcher;
import net.datatp.xhtml.fetcher.HttpClientFetcher;

public class OtherContentExtractorUnitTest {
  static String[] EXPECT_OTHER_TAG_DETAIL = {"content:other", "content:detail"} ;

  static URLVerifier SUZUKI = new URLVerifier(
      "Cùng Suzuki vượt qua bão giá",
      "http://www.suzuki.com.vn/vi-VN/zone/4/item/537/sitem.cco",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier HONDA = new URLVerifier(
      "Honda Việt Nam chào mừng chiếc Xe máy thứ 10 triệu",
      "http://www.honda.com.vn/Honda/News/News_Detail/tabid/672/newsid/2597/seo/Honda-Viet-Nam-chao-mung-chiec-Xe-may-thu-10-trieu/Default.aspx",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  ); 
  
  static URLVerifier LUATHOANGMINH = new URLVerifier(
      "Mua bán, sáp nhập doanh nghiệp",
      "http://www.luathoangminh.com/linh-vuc-tu-van-hoat-dong/mua-ban-sap-nhap-tai-co-cau-doanh-nghiep/1919-mua-ban-sap-nhap-doanh-nghiep.html",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier VNPACK = new URLVerifier(
      "CHẤT PHỦ SINH THÁI MỚI SẼ THAY THẾ MÀNG BAO BÌ NHỰA",
      "http://www.vnpack.com.vn/tin-tuc-cong-nghe/173-chat-phu-sinh-thai-moi-se-thay-the-mang-bao-bi-nhua.html",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier LACVIET = new URLVerifier(
      "Lạc Việt giới thiệu gói giải pháp AccNet ERP",
      "http://www.lacviet.com.vn/lcmsweb/Default.aspx?pageid=170&mid=314&action=docdetailview&breadcrumb=117&intSetItemId=117&intDocId=182",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier INTAICHINH = new URLVerifier(
      "GIỚI THIỆU CÔNG TY",
      "http://www.intaichinh.com.vn/module/news/ViewContent.asp?langid=2&ID=2191",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier TUVANTAICHINH = new URLVerifier(
      "Tạm đình chỉ hoạt động cung cấp dịch vụ định giá doanh nghiệp của 17 tổ chức tư vấn",
      "http://www.aasc.com.vn/NewsDetails.aspx?NewsID=784",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier VINACHEM = new URLVerifier(
      "Sản lượng lốp xe của Trung Quốc tăng trong tháng 8",
      "http://www.vinachem.com.vn/Desktop.aspx/Tin-tuc/Tin-thi-truong-san-pham/San_luong_lop_xe_cua_Trung_Quoc_tang_trong_thang_8/",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier BIDIPHAR = new URLVerifier(
      "Bidiphar tổ chức khám và cấp phát thuốc miễn phí cho các đối tượng chính sách, các hộ nghèo.",
      "http://www.bidiphar.com/index.php?mod=newsdetail&sc=2&ns=474",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  static URLVerifier BAOHIEMAIA = new URLVerifier(
      "Tuyển chọn Đại lý Bảo hiểm",
      "http://www.aia.com.vn/vn/recruitment/for-consultants/agents/agent-recruitment",
      DocumentExtractor.Type.other, EXPECT_OTHER_TAG_DETAIL
  );
  
  private void verifyAll(Fetcher fetcher) throws Exception {
    URLVerifier[] all = {
       SUZUKI, HONDA, LUATHOANGMINH, VNPACK, LACVIET,
       INTAICHINH, TUVANTAICHINH, VINACHEM, BIDIPHAR, BAOHIEMAIA
    };
    for(URLVerifier sel : all) sel.verify(fetcher, false) ;
  }
  
  @Test
  public void test() throws Exception {
    Fetcher fetcher = new HttpClientFetcher();
    //verifyAll(fetcher) ;
    //SUZUKI.verify(fetcher, true);
    //HONDA.verify(fetcher, true);
    //LUATHOANGMINH.verify(fetcher, true) ;
    //VNPACK.verify(fetcher, true) ;
    //LACVIET.verify(fetcher, true);
    //INTAICHINH.verify(fetcher, true) ;
    //TUVANTAICHINH.verify(fetcher, true);
    //VINACHEM.verify(fetcher, true) ;
    //BIDIPHAR.verify(fetcher, true);
    BAOHIEMAIA.verify(fetcher, true) ;
  }
}
