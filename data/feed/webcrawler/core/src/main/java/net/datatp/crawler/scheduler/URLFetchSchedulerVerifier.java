package net.datatp.crawler.scheduler;

import org.slf4j.Logger;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 10, 2010  
 */
public class URLFetchSchedulerVerifier {
  long pUrlCount = -1;
  long pWaittingCount = -1 ;

  public void verify(Logger logger, long urlCount, long waittingCount) {
    if(pUrlCount > urlCount) {
      logger.error("Expect frequency of url is greater or equals the previous frequency of url count") ;
    }
    pUrlCount = urlCount ;
    pWaittingCount = waittingCount ;
  }
}
