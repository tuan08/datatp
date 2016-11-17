package net.datatp.webui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.datatp.util.log.LoggerFactory;

public class DummyUnitTest {
  
  @Before
  public void setup() throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");
  }

  @After
  public void teardown() throws Exception {
  }
  
  @Test
  public void test() throws Exception {
  }
}