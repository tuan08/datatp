package net.datatp.xhtml.dom.extract;

import org.junit.Test;

import net.datatp.xhtml.SimpleHttpFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ClassifiedContentExtractorUnitTest {
  static String[] EXPECT_CLASSIFIED_TAG_DETAIL = {"content:classified", "content:detail"} ;

  static URLVerifier RAOVAT = new URLVerifier(
      "IP Camera không dây giá tốt nhất Việt Nam",
      "http://raovat.net/xem/1239699-IP-Camera-khong-day-gia-tot-nhat-Viet-Nam.html",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier RONGBAY = new URLVerifier(
      "CHUYÊN CUNG CẤP NHẪN NỮ, NHẪN NỮ THỜI TRANG CAO CẤP, SANG TRỌNG QUÝ PHÁI TÔN VINH THÊM VẺ ĐẸP CỦA PHÁI NỮ. (222)",
      "http://rongbay.com/Binh-Duong/tai-dinh-cu-hoa-loi-lo-C22-gia-6tr-do-c15-raovat-13149468.html",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier ENBAC = new URLVerifier(
      "Áo thu đông mới nhất 2011 cho các boy đây.",
      "http://enbac.com/Thoi-trang-Nam/p1087323/Ao-thu-dong-moi-nhat-2011-cho-cac-boy-day.html",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier RAOVAT123 = new URLVerifier(
      "Máy hút mùi Faster FS-700GS khuyến mại hấp dẫn tại",
      "http://www.raovat123.com/t.4537329.may-hut-mui-faster-fs-700gs-khuyen-mai-hap-dan-tai.html",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier RAOVATCOM = new URLVerifier(
      "MÁY PHOTOCOPY XEROX DOCUCENTRE 1055DC",
      "http://raovat.com/?rv=detail&idrv=367307&idcate=44&tt=MAY+PHOTOCOPY+XEROX+DOCUCENTRE+1055DC",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier NEGEMART = new URLVerifier(
      "Túi Balo Teen Da Đẹp rẻ Có Sẵn",
      "http://www.negemart.vn/nguyen-pham-hong-thuy/25116-tui-balo-teen-da-dep-re-co-san/",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier RAOVATNGAY = new URLVerifier(
      "Thang máy Mitsubishi ThaiLand",
      "http://raovatngay.com/tin-chi-tiet/599/7659/-thang-may-mitsubishi-thailand-wwwthucdaythuonghieu--.html",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier RAOVATCUATUI = new URLVerifier(
      "BÁN ĐẤT THỔ CƯ - CÓ SỔ ĐỎ Tại Biên Hòa Đồng Nai",
      "http://raovatcuatui.com/tin-rao-vat/22579/ban-dat-tho-cu---co-so-do-tai-bien-hoa-dong-nai",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier MUONRAOVAT = new URLVerifier(
      "Dell Inspiron N5010 Intel Core i3-370M 2.4GHz",
      "http://www.muonraovat.com/sp/chi_tiet/237/4375-dell-inspiron-n5010-intel-core-i3-370m-2-4ghz",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  static URLVerifier RAOVATDTDD = new URLVerifier(
      "Iphon 4 hàng xách tay moi 100% HB=24",
      "http://www.raovatdtdd.com/raovat/tinraovat/82864/iphon-4-hang-xach-tay-moi-100-hb-24th-gia-3-2t.html",
      DocumentExtractor.Type.classified, EXPECT_CLASSIFIED_TAG_DETAIL
  );
  
  private void verifyAll(SimpleHttpFetcher fetcher) throws Exception {
    URLVerifier[] all = {
       RAOVAT, RONGBAY, ENBAC, RAOVAT123, RAOVATCOM,
       NEGEMART, RAOVATNGAY, RAOVATCUATUI, MUONRAOVAT, RAOVATDTDD
    };
    for(URLVerifier sel : all) sel.verify(fetcher, false) ;
  }
  
  @Test
  public void test() throws Exception {
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    //verifyAll(fetcher) ;
    //RAOVAT.verify(fetcher, true) ;
    //RONGBAY.verify(fetcher, true) ;
    //ENBAC.verify(fetcher, true) ;
    
    //RAOVAT123.verify(fetcher, true) ;
    //RAOVATCOM.verify(fetcher, true) ;
    //NEGEMART.verify(fetcher, true) ;
    //RAOVATNGAY.verify(fetcher, true);
    //RAOVATCUATUI.verify(fetcher, true) ;
    MUONRAOVAT.verify(fetcher, true) ;
    //RAOVATDTDD.verify(fetcher, true) ;
  }
}