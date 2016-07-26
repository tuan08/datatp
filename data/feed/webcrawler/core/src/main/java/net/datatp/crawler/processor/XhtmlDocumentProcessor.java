package net.datatp.crawler.processor;

import net.datatp.xhtml.XhtmlDocument;

public interface XhtmlDocumentProcessor {
  static public XhtmlDocumentProcessor NONE = new XhtmlDocumentProcessor() {
    @Override
    public void process(XhtmlDocument xdoc) throws Exception {
    }
  };
  
  static public XhtmlDocumentProcessor PRINT_URL = new XhtmlDocumentProcessor() {
    @Override
    public void process(XhtmlDocument xdoc) throws Exception {
      System.out.println(xdoc.getUrl());
    }
  };
  
  public void process(XhtmlDocument xdoc) throws Exception;

}
