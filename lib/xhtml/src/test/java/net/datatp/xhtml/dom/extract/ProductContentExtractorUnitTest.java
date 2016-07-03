package net.datatp.xhtml.dom.extract;

import org.junit.Test;

import net.datatp.xhtml.dom.extract.DocumentExtractor;
import net.datatp.xhtml.fetcher.Fetcher;
import net.datatp.xhtml.fetcher.HttpClientFetcher;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ProductContentExtractorUnitTest {
	static String[] EXPECT_PRODUCT_TAG_DETAIL = {"content:product", "content:detail"} ;
	
  static URLVerifier ANHNGHIA = new URLVerifier(
  		"Toshiba Satellite L645 1058U (PSK0JL-00R001)",
  		"http://anhnghia.com/view_product.aspx?cid=908&pid=6458",
  		DocumentExtractor.Type.product, EXPECT_PRODUCT_TAG_DETAIL
  );
  
  static URLVerifier PICO = new URLVerifier(
      "TIVI LED Toshiba 40AL10V-40\", FULL HD",
      "http://www.pico.vn/ProductDetail.aspx?ProductId=14702",
      DocumentExtractor.Type.product, EXPECT_PRODUCT_TAG_DETAIL
  );
  
  static URLVerifier TRANANH = new URLVerifier(
  		"Máy tính để bàn TRANANH PC - Model: SVi334",
  		"http://www.trananh.vn/May-tinh-de-ban-TRANANH-PC-Model-SVi334_19199.html",
  		DocumentExtractor.Type.product, EXPECT_PRODUCT_TAG_DETAIL
  );
  
  static URLVerifier BEN = new URLVerifier(
      "Lenovo G470 ( 59-303611 ) Intel Pentium Processor B940 (2.0GHz/1333MHz/2M L3 Cache), 2G DDR3, 500GB, 14\"",
      "http://ben.com.vn/mains.aspx?MNU=266&Type=Product&ID=13371",
      DocumentExtractor.Type.product, EXPECT_PRODUCT_TAG_DETAIL
  );
  
  static URLVerifier MAIHOANG = new URLVerifier(
      "HP ProBook 4530s (i3-2310M) Kèm theo quà tặng tại Mai Hoàng",
      "http://www.maihoang.com.vn/ProductDetail.aspx?tuto=8698&cate=376",
      DocumentExtractor.Type.product, EXPECT_PRODUCT_TAG_DETAIL
  );
  
  private void verifyAll(Fetcher fetcher) throws Exception {
  	URLVerifier[] all = {
  		ANHNGHIA, PICO, TRANANH, BEN	
  	};
  	for(URLVerifier sel : all) sel.verify(fetcher, false) ;
  }
  
	@Test
  public void test() throws Exception {
  	Fetcher fetcher = new HttpClientFetcher();
  	//verifyAll(fetcher) ;
  	//PICO.verify(fetcher, true) ;
  	TRANANH.verify(fetcher , true);
  	//BEN.verify(fetcher, true) ;
  	//MAIHOANG.verify(fetcher, true) ;
	}
}