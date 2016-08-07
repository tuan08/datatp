package net.datatp.search;

import net.datatp.es.ESClient;
import net.datatp.es.ESObjectClient;
import net.datatp.xhtml.XDoc;

public class ESXDocSearcher {
  private ESClient             esclient;
  private ESObjectClient<XDoc> esXDocClient;

  public ESXDocSearcher(String index, String[] esAddress) throws Exception {
    this.esclient     = new ESClient(esAddress);
    this.esXDocClient = new ESObjectClient<>(esclient, index, XDoc.class) ;
  }
}
