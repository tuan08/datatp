package net.datatp.util.log;

import java.net.URL;

import org.junit.Test;
import org.slf4j.Logger;

import net.datatp.util.log.LoggerFactory;

public class LoggerFactoryUnitTest {
  @Test
  public void testLoggerFactory() throws Exception {
    LoggerFactory.log4jConfigure(new URL("file:src/test/java/net/datatp/util/log/log4j.properties"));
    LoggerFactory lFactory = new LoggerFactory("[vm=vm-master].[module=NeverwinterDP] ");
    Logger logger = lFactory.getLogger(getClass()) ;
    logger.info("This is a test");
    
    lFactory = new LoggerFactory();
    System.setProperty("hostname", "localhost");
    LoggerFactory.log4jConfigure(new URL("file:src/test/java/net/datatp/util/log/log4j-update.properties"));
    logger.info("This is a test");
  }
}
