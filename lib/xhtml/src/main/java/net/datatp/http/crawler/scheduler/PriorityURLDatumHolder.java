package net.datatp.http.crawler.scheduler;

import java.util.Comparator;

import net.datatp.http.crawler.site.SiteContext;
import net.datatp.http.crawler.urldb.URLDatum;
import net.datatp.util.HeapTree;


public class PriorityURLDatumHolder {
  private SiteContext context ;
  private HeapTree<URLDatum> heap  ;
  private int insertCount ;

  public PriorityURLDatumHolder(SiteContext context, int maxSchedule, int maxPriorityDeep) {
    this.context = context ;
    URLDatumComparator comparator = new URLDatumComparator(maxPriorityDeep) ;
    heap = new HeapTree<URLDatum>(maxSchedule, comparator) ;
  }

  public PriorityURLDatumHolder(int maxSchedule, int maxPriorityDeep) {
    URLDatumComparator comparator = new URLDatumComparator(maxPriorityDeep) ;
    heap = new HeapTree<URLDatum>(maxSchedule, comparator) ;
  }

  public SiteContext getSiteConfigContext() { return this.context ; }

  public int getSize() { return heap.size() ;}

  public int getDelayCount() { return this.insertCount - heap.size() ; }

  public void insert(URLDatum datum) {
    heap.insert(datum);
    this.insertCount++ ;
  }

  public URLDatum[] getURLDatum() {
    return heap.toArray(new URLDatum[heap.size()]) ;
  }

  static public class URLDatumComparator implements Comparator<URLDatum> {
    private int maxPriorityDeep ;

    URLDatumComparator(int maxPriorityDeep) {
      this.maxPriorityDeep = maxPriorityDeep ;
    }

    public int compare(URLDatum url1, URLDatum url2) {
      int deep1 = url1.getDeep() ;
      int deep2 = url2.getDeep() ;
      int result = 0 ;
      if(deep1 > maxPriorityDeep && deep2 > maxPriorityDeep) {
        result = url1.getFetchCount() - url2.getFetchCount() ;
      } else if(deep1 < maxPriorityDeep && deep2 < maxPriorityDeep) {
        result = url1.getFetchCount() - url2.getFetchCount() ;
      } else {
        result = deep1 - deep2;
      }
      return -result ;
    }
  }
}