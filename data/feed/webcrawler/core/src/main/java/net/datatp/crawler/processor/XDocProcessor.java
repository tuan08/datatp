package net.datatp.crawler.processor;

import net.datatp.xhtml.XDoc;

public interface XDocProcessor {
  static public XDocProcessor NONE = new XDocProcessor() {
    @Override
    public void process(XDoc xdoc) throws Exception {
    }
  };
  
  static public XDocProcessor PRINT_URL = new XDocProcessor() {
    @Override
    public void process(XDoc xdoc) throws Exception {
      System.out.println(xdoc.attr("url"));
    }
  };
  
  public void process(XDoc xdoc) throws Exception;

}
