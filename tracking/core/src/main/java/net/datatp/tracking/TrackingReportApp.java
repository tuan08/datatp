package net.datatp.tracking;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import net.datattp.registry.Registry;
import net.datattp.registry.RegistryConfig;
import net.datattp.registry.RegistryException;
import net.datattp.registry.zk.RegistryImpl;

public class TrackingReportApp {
  @Parameter(names = "--zk-connect", description = "The zk connect string")
  private String zkConnects = "localhost:2181";
  
  @Parameter(names = "--tracking-path", description = "The zk connect string")
  private String trackingPath = "/tracking";
  
  private Registry registry ;
  private TrackingRegistry trackingRegistry;
  
  public TrackingReportApp(String[] args) throws RegistryException {
    new JCommander(this, args);
    RegistryConfig regConfig = RegistryConfig.getDefault();
    regConfig.setConnect(zkConnects);
    registry =  new RegistryImpl(regConfig).connect() ;
    trackingRegistry = new TrackingRegistry(registry, trackingPath, true);
  }
  
  public void report() throws Exception {
    List<TrackingMessageReport> generatedReports = trackingRegistry.getGeneratorReports();
    List<TrackingMessageReport> validatedReports = trackingRegistry.getValidatorReports();
    System.out.println(TrackingMessageReport.getFormattedReport("Generated Report", generatedReports));
    System.out.println(TrackingMessageReport.getFormattedReport("Validated Report", validatedReports));
  }
  
  public void shutdown() throws Exception {
    registry.shutdown();
  }
  
  static public void main(String[] args) throws Exception {
    TrackingReportApp app = new TrackingReportApp(args);
    app.report();
    app.shutdown();
  }
  
}
