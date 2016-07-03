package net.datatp.xhtml.fetcher;

import net.datatp.xhtml.XhtmlDocument;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 25, 2010  
 */
public interface Fetcher {
  public XhtmlDocument fetch(String url) throws Exception ;
}
