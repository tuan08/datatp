package net.datatp.webcrawler.site;

import java.io.Serializable ;


import net.datatp.util.text.StringMatcher;
import net.datatp.webcrawler.site.SiteContext.Modify;
import net.datatp.webcrawler.urldb.URLDatumStatisticMap;

/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class SiteContextFilter implements Serializable {
  public String getName() { return getClass().getSimpleName() ; }

  abstract public boolean include(int index, SiteContext context) ; 

  static public class IndexRangeFilter extends SiteContextFilter {
    private int from , to ;

    public IndexRangeFilter(int from, int to) {
      this.from = from ;
      this.to = to ;
    }

    public boolean include(int index, SiteContext context) {
      return index >= from && index < to ;
    }
  }

  static public class SiteNameFilter extends SiteContextFilter {
    private StringMatcher matcher ;

    public SiteNameFilter(String exp) {
      matcher = new StringMatcher(exp) ;
    }

    public boolean include(int index, SiteContext context) {
      return matcher.matches(context.getSiteConfig().getHostname()) ;
    }
  }

  static public class ResponseCodeFilter extends SiteContextFilter {
    private int rcGroup ;
    private long from , to ;

    public ResponseCodeFilter(int rcGroup, int from, int to) {
      this.rcGroup = rcGroup ;
      this.from = from ;
      this.to = to ;
    }

    public boolean include(int index, SiteContext context) {
      String keyRC = "RC"+rcGroup ;
      if(rcGroup == 10000) keyRC = "Unknown" ;
      long value = context.getAttribute(URLDatumStatisticMap.class).getStatisticMap().getStatistics().get("Response Code").get(keyRC).getFrequency();
      return (value >= from) && (value < to) ;
    }
  }

  static public class FetchCountFilter extends SiteContextFilter {
    private String fcGroup ;
    private long from , to ;

    public FetchCountFilter(String fcGroup, int from, int to) {
      this.fcGroup = fcGroup ;
      this.from = from ;
      this.to = to ;
    }

    public boolean include(int index, SiteContext context) {
      String keyRC = "FC"+fcGroup ;
      long value = context.getAttribute(URLDatumStatisticMap.class).getStatisticMap().getStatistics().get("Fetch Count").get(keyRC).getFrequency();
      return (value >= from) && (value < to) ;
    }
  }

  static public class ModifyFilter extends SiteContextFilter {
    private Modify modify;

    public ModifyFilter(Modify modify) {
      this.modify = modify;
    }

    public boolean include(int index, SiteContext context) {
      Modify modify = context.getModify();
      return this.modify == modify;
    }
  }

  static public class StatusFilter extends SiteContextFilter {
    private String status;

    public StatusFilter(String status) {
      this.status = status;
    }

    public boolean include(int index, SiteContext context) {
      String status = context.getSiteConfig().getStatus();
      return this.status.equals(status);
    }
  }

  static public class URLCountFilter extends SiteContextFilter {
    private long count;
    private int from, to;

    public URLCountFilter(int from, int to) {
      this.from = from;
      this.to = to;
    }

    public boolean include(int index, SiteContext context) {
      count = context.getAttribute(URLDatumStatisticMap.class).getStatisticMap().getStatistics().get("Fetch Status").get("All").getFrequency() ;
      return (count >= from) && (count < to) ;
    }
  }

  static public class URLRedirectCountFilter extends SiteContextFilter {
    private long count;
    private int from, to;

    public URLRedirectCountFilter(int from, int to) {
      this.from = from;
      this.to = to;
    }

    public boolean include(int index, SiteContext context) {
      count = context.getAttribute(URLDatumStatisticMap.class).getStatisticMap().getStatistics().get("Fetch Status").get("wRedirect").getFrequency() ;
      return (count >= from) && (count < to) ;
    }
  }

  static public class ToCommitFilter extends SiteContextFilter {
    private Modify modify;

    public boolean include(int index, SiteContext context) {
      modify = context.getModify();
      return (modify != Modify.NONE);
    }
  }
}