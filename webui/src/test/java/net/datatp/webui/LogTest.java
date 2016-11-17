package net.datatp.webui;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
  static {
    System.setProperty("log4j.configurationFile", "src/test/resources/log4j2.yml");
  }
  
  final Logger logger = LoggerFactory.getLogger(getClass());
  
  @Test
  public void test() throws Exception {
    logger.info("Test log4j2 configuration info");
    logger.warn("Test log4j2 configuration warn");
    logger.error("Test log4j2 configuration error");
  }
}