package net.datatp.crawler.site;

import org.junit.Test;

import net.datatp.util.dataformat.DataSerializer;

public class SiteConfigUnitTest {
  @Test
  public void test() throws Exception {
    SiteConfig siteConfig = 
        new SiteConfig("vietnam", "vnexpress.net", "http://vnexpress.net", 3).
        setExtractConfig(ExtractConfig.article());
    String json = DataSerializer.JSON.toString(siteConfig);
    System.out.println(json);
    siteConfig = DataSerializer.JSON.fromString(json, SiteConfig.class);
  }
}
