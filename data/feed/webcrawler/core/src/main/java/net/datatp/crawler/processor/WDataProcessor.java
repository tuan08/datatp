package net.datatp.crawler.processor;

import net.datatp.xhtml.xpath.WDataContext;

public interface WDataProcessor {
  static public WDataProcessor NONE = new WDataProcessor() {
    @Override
    public void process(WDataContext context) throws Exception {
    }
  };
  
  static public WDataProcessor PRINT_URL = new WDataProcessor() {
    @Override
    public void process(WDataContext context) throws Exception {
      System.out.println(context.getWdata().getUrl());
    }
  };
  
  public void process(WDataContext context) throws Exception;

}
