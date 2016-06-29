package net.datatp.sys;

import java.util.Random;

import org.junit.Test;

import net.datatp.model.sys.JHiccup;
import net.datatp.sys.JHiccupMeter;

public class JHiccupMeterUnitTest {
  @Test
  public void testJHiccupMeter() throws Exception {
    JHiccupMeter hiccupMeter =  new JHiccupMeter("localhost", 50L/*resolutionMs*/);
    Random rand = new Random();
    for(int i = 0; i < 5; i++) {
      JHiccup hiccupInfo = hiccupMeter.getHiccup();
      System.out.println(JHiccup.getFormattedText(hiccupInfo));
      for(int j = 0; j < 1000; j++) {
        //produce some data so gc will pause jvm to collect the objects.
        byte[] data = new byte[1024 * 1024]; 
        rand.nextBytes(data);
      }
      Thread.sleep(5000);
    }
  }
}