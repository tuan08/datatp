package net.datatp.crawler.distributed.integration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.datatp.xhtml.WData;
/**
 * Author: Tuan Nguyen$ 
 *         tuan08@gmail.com
 **/
//@MessageEndpoint
public class DocumentConsumerLogger {
  private int maxLogPerSite =  3000 ;
  private Map<String, LinkedList<DocumentLog>> logHolder = new HashMap<String, LinkedList<DocumentLog>>() ;

  //@ServiceActivator(inputChannel = "CrawlerOutputChannel")
  public void consume(WData wpData) throws Exception {
    String id  = wpData.getUrl() ;
    String site = id.substring(0, id.indexOf(":")) ;
    LinkedList<DocumentLog> siteLogHolder = logHolder.get(site) ;
    if(siteLogHolder == null) {
      siteLogHolder = new LinkedList<DocumentLog>() ;
      logHolder.put(site, siteLogHolder) ;
    }
    siteLogHolder.add(new DocumentLog(wpData)) ;
    if(siteLogHolder.size() > maxLogPerSite) {
      siteLogHolder.removeFirst() ;
    }
    //System.out.println("consume: " + doc.getUrl());
  }
  
  public Map<String, LinkedList<DocumentLog>> getLogs() {
    return this.logHolder ; 
  }

  static public class DocumentLog {
    private String url ;
    private int responseCode ;
    private long   contentLength ;
    
    public String getUrl() { return url ; }
    
    public long getContentLength()  { return contentLength ; }
    
    public int getResponseCode() { return responseCode ; }
    
    public DocumentLog(WData wpData) {
      url = wpData.getUrl();
      responseCode  = 200 ;
      contentLength = (long)wpData.getDataAsXhtml().length();
    }
  }
}