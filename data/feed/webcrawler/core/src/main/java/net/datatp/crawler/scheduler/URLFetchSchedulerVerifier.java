package net.datatp.crawler.scheduler;

import org.slf4j.Logger;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 10, 2010  
 */
public class URLFetchSchedulerVerifier {
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
