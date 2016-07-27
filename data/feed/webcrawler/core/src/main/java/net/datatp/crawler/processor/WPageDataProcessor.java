package net.datatp.crawler.processor;

import net.datatp.xhtml.WData;

public interface WPageDataProcessor {
  static public WPageDataProcessor NONE = new WPageDataProcessor() {
    @Override
    public void process(WData xdoc) throws Exception {
    }
  };
  
  static public WPageDataProcessor PRINT_URL = new WPageDataProcessor() {
    @Override
    public void process(WData xdoc) throws Exception {
      System.out.println(xdoc.getUrl());
    }
  };
  
  public void process(WData xdoc) throws Exception;

}
