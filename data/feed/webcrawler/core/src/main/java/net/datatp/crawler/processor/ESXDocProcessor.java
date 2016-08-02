package net.datatp.crawler.processor;

import net.datatp.es.ESClient;
import net.datatp.es.ESObjectClient;
import net.datatp.xhtml.XDoc;

public class ESXDocProcessor implements XDocProcessor {
  private String               index;
  private ESClient             esclient;
  private ESObjectClient<XDoc> esXDocClient;

  public ESXDocProcessor(String index, String[] esAddress) throws Exception {
    this.index        = index;
    this.esclient     = new ESClient(esAddress);
    this.esXDocClient = new ESObjectClient<>(esclient, index, XDoc.class) ;
    if (!esXDocClient.isCreated()) {
      esXDocClient.createIndexWithResourceConfig(
        "net/datatp/crawler/XDoc.setting.json",
        "net/datatp/crawler/XDoc.mapping.json"
      );
    }
  }
  
  @Override
  public void process(XDoc xdoc) throws Exception {
    esXDocClient.put(xdoc, xdoc.attr("md5Id"));
  }
}
