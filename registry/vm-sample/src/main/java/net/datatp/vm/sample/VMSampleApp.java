package net.datatp.vm.sample;

import net.datatp.vm.VMApp;

public class VMSampleApp extends VMApp {
  @Override
  public void run() throws Exception {
    try {
      int count = 0 ;
      while(count < 30) {
        System.out.println("Hello VM Sample App!!!");
        Thread.sleep(1000);
        count++ ;
      }
    } catch(InterruptedException ex) {
    }
  }
}