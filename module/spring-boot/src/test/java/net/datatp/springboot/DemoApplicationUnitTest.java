package net.datatp.springboot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.datatp.springboot.app1.Application1;
import net.datatp.springboot.app2.Application2;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
  classes = {Application1.class, Application2.class}
)
public class DemoApplicationUnitTest {
  
  @Test
  public void contextLoads() {
  }
}