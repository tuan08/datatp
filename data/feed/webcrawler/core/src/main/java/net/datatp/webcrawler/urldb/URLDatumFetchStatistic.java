package net.datatp.webcrawler.urldb;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.datatp.webcrawler.ErrorCode;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jul 7, 2010  
 */
public class URLDatumFetchStatistic implements Serializable {
  private Map<Integer, Integer> rcs = new HashMap<Integer, Integer>() ;
  private Map<Integer, Integer> errors = new HashMap<Integer, Integer>() ;
  private Map<Integer, Integer> pageTypes = new HashMap<Integer, Integer>() ;

  public int getResponseCodeGroup100() { return getResponseCodeGroup(100, 200) ; }
  
  public int getResponseCodeGroup200() { return getResponseCodeGroup(200, 300) ; }
  
  public int getResponseCodeGroup300() { return getResponseCodeGroup(300, 400) ; }
  
  public int getResponseCodeGroup400() { return getResponseCodeGroup(400, 500) ; }
  
  public int getResponseCodeGroup500() { return getResponseCodeGroup(500, 600) ; }
  
  public int getResponseCodeGroup10000() { return getResponseCodeGroup(10000, 20000) ; }
  
  public int getErrors() { return getErrorCodeGroup(1, 128) ; }
  
  private void incrCount(Map<Integer, Integer> map, int code) {
    Integer key = new Integer(code) ;
    Integer count = (Integer) map.get(key) ;
    if(count == null) {
      map.put(key, 1) ;
    } else {
    	map.put(key, count.intValue() + 1) ;
    }
  }
  
  public int getPageListType() {
  	int total = 0 ;
  	Integer count = ((Integer)pageTypes.get(URLDatum.PAGE_TYPE_LIST)) ;
  	if(count != null) total = count.intValue() ;
  	return total ;
  }
  
  public int getPageDetailType() {
    int total = 0 ;
    Integer count = ((Integer)pageTypes.get(URLDatum.PAGE_TYPE_DETAIL)) ;
    if(count != null) total = count.intValue() ;
    return total ;
  }
  
  public void log(URLDatum datum) {
    incrCount(rcs, datum.getLastResponseCode()) ;
    if(datum.getLastErrorCode() != ErrorCode.ERROR_TYPE_NONE) {
      incrCount(errors, datum.getLastErrorCode()) ;
    }
    incrCount(pageTypes, (int)datum.getPageType()) ;
  }
  
  public int getResponseCodeGroup(int from, int to) {
    int total = 0 ;
    Iterator<Map.Entry<Integer, Integer>> i = rcs.entrySet().iterator() ;
    while(i.hasNext()) {
    	Map.Entry<Integer, Integer> entry = i.next() ;
    	int code = ((Integer) entry.getKey()).intValue() ;
    	if(code >= from && code < to) {
    		total += ((Integer)entry.getValue()).intValue() ;
    	}
    }
    return total ;
  }
  
  public int getErrorCodeGroup(int from, int to) {
    int total = 0 ;
    Iterator<Map.Entry<Integer, Integer>> i = errors.entrySet().iterator() ;
    while(i.hasNext()) {
      Map.Entry<Integer, Integer> entry = i.next() ;
      int code = ((Integer) entry.getKey()).intValue() ;
      if(code >= from && code < to) {
        total += ((Integer)entry.getValue()).intValue() ;
      }
    }
    return total ;
  }
}