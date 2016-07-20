package net.datatp.xhtml.dom.extract;

import org.junit.Test;

import net.datatp.http.SimpleHttpFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JobContentExtractorUnitTest {
  static String[] EXPECT_JOB_TAG_DETAIL = {"content:job", "content:detail"} ;

  static URLVerifier UNGVIEN = new URLVerifier(
      "Công ty CP Truyền Thông Kim Cương",
      "http://ungvien.com.vn/employee/employer/23085-Cong-ty-CP-Truyen-Thong-Kim-Cuong.html",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier VIETNAMWORK = new URLVerifier(
      "Cán Bộ Công Nghệ Thông Tin",
      "http://www.vietnamworks.com/jobseekers/jobdetail.php?jobid=320626&a=fromsearch",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier VIECLAM = new URLVerifier(
      "Việc Làm Phù Hợp Cho Sinh Viên Mới Tốt Nghiệp",
      "http://www.timviecnhanh.com/vieclam/congviec/2466145/viec-lam-phu-hop-cho-sinh-vien-moi-tot-nghiep.html",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier VIECLAM24H = new URLVerifier(
      "NV KD quảng cáo online tại HN",
      "http://vieclam.24h.com.vn/ban-hang/nv-kd-quang-cao-online-tai-hn-c63p2id839178.html",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier CAREERLINK = new URLVerifier(
      "NV Lễ Tân Tiếng Nhật Kiêm Chăm Sóc Khách Hàng",
      "https://www.careerlink.vn/tim-viec-lam/nv-le-tan-tieng-nhat-kiem-cham-soc-khach-hang/896193",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier VIECLAMTUOITRE = new URLVerifier(
      "Biên Phiên Dịch Tiếng Nhật",
      "http://vieclam.tuoitre.vn/vi/ung-vien/bien-phien-dich-tieng-nhat.35A6E3C0.html",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier TUYENDUNG = new URLVerifier(
      "CÔNG TY CỔ PHẦN CHỨNG KHOÁN THĂNG LONG",
      "http://tuyendung.com.vn/timvieclam/43356-truong-phong-hanh-chinh-tong-hop-cn-hai-phong.aspx",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier VIECLAMLAODONG = new URLVerifier(
      "Công ty Lắp máy điện nước LICOGI",
      "http://vieclam.laodong.com.vn/?page=ntd_profile&portal=vieclam&job_id=4867",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  //TODO: Fail tagger (content:unknown-type) ;
  static URLVerifier VIECLAMTUYENDUNG = new URLVerifier(
      "CÔNG TY CỔ PHẦN TRÒ CHƠI GIÁO DỤC TRỰC TUYẾN",
      "http://vieclamtuyendung.net/ungvien/show_search.php#congviecchitiet",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  static URLVerifier VIECLAMNLD = new URLVerifier(
      "NHÂN VIÊN TUYỂN SINH",
      "http://vieclam.nld.com.vn/?view=jobs&id=1143371",
      DocumentExtractor.Type.job, EXPECT_JOB_TAG_DETAIL
      );

  private void verifyAll(SimpleHttpFetcher fetcher) throws Exception {
    URLVerifier[] all = {
        UNGVIEN, VIETNAMWORK, VIECLAM, VIECLAM24H, CAREERLINK,
        VIECLAMTUOITRE, TUYENDUNG, VIECLAMLAODONG, VIECLAMTUYENDUNG, VIECLAMNLD 
    };
    for(URLVerifier sel : all) sel.verify(fetcher, false) ;
  }

  @Test
  public void test() throws Exception {
    SimpleHttpFetcher fetcher = new SimpleHttpFetcher();
    //verifyAll(fetcher) ;
    //UNGVIEN.verify(fetcher, true) ;
    //VIETNAMWORK.verify(fetcher, true) ;
    //VIECLAM.verify(fetcher, true) ;
    //VIECLAM24H.verify(fetcher, true) ;
    CAREERLINK.verify(fetcher, true) ;
    //VIECLAMTUOITRE.verify(fetcher, true) ;
    //TUYENDUNG.verify(fetcher, true) ;
    //VIECLAMLAODONG.verify(fetcher, true) ;
    //VIECLAMNLD.verify(fetcher, true) ;
    //VIECLAMTUYENDUNG.verify(fetcher, true);
  }
}