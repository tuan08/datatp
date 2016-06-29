package net.datatp.storage.batchdb;

import org.apache.hadoop.mapreduce.TaskInputOutputContext;

public class MapReduceReporter implements Reporter {
  private TaskInputOutputContext<?,?,?,?> context ;
  private String databaseLoc ;

  public MapReduceReporter(TaskInputOutputContext<?,?,?,?> context, String databaseLoc) {
    this.context = context ;
    this.databaseLoc = databaseLoc ;
  }

  public void progress() { context.progress() ; }

  public void increment(String name, int amount) {
    context.getCounter(databaseLoc, name).increment(amount) ;
  }
}