package net.datatp.webcrawler.fetcher;

import net.datatp.webcrawler.urldb.URLDatum;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 14, 2010  
 */
public interface Fetcher {
  final static public long DEFAULT_TIMEOUT = 60 * 60 * 1000 ; //1 hour
  public void fetch(URLDatum datum) throws Exception ;
}
