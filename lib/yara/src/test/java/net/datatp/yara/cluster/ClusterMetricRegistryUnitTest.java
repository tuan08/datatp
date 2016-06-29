package net.datatp.yara.cluster;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import net.datatp.yara.MetricRegistry;
import net.datatp.yara.cluster.ClusterMetricPrinter;
import net.datatp.yara.cluster.ClusterMetricRegistry;

public class ClusterMetricRegistryUnitTest {
  @Test
  public void testBasic() throws IOException {
    MetricRegistry server1 = new MetricRegistry("server1") ;
    MetricRegistry server2 = new MetricRegistry("server2") ;
    Random rand = new Random() ;
    for(int i = 0; i < 1000; i++) {
      long duration = rand.nextInt(i + 1) ;
      if(duration % 3 == 0) {
        server1.getCounter("method call counter").incr() ;
        server1.getTimer("method call timer").update(duration, TimeUnit.NANOSECONDS);
      } else {
        server2.getCounter("method call counter").incr() ;
        server2.getTimer("method call timer").update(duration, TimeUnit.NANOSECONDS);
      }
    }
    ClusterMetricRegistry registry = new ClusterMetricRegistry() ;
    registry.update(server1);
    registry.update(server2);
    
    ClusterMetricPrinter printer = new ClusterMetricPrinter() ;
    printer.print(registry);
  }
}
