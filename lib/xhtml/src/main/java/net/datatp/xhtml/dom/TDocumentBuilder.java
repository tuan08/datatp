package net.datatp.xhtml.dom;

import java.io.IOException;


/**
 * $Author: Tuan Nguyen$ 
 **/
public interface TDocumentBuilder {
  public TNode  toTNode(String html) throws Exception ;
  public String reformat(String html) throws IOException ;
}