package net.datatp.webcrawler.fetch;

import org.slf4j.Logger;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Jul 10, 2010  
 */
public class URLDatumFetchSchedulerVerifier {
  int pUrlCount = -1;
  int pWaittingCount = -1 ;

  public void verify(Logger logger, int urlCount, int waittingCount) {
    if(pUrlCount > urlCount) {
      logger.error("Expect frequency of url is greater or equals the previous frequency of url count") ;
    }
    pUrlCount = urlCount ;
    pWaittingCount = waittingCount ;
  }
}
