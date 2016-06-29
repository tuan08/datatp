package net.datatp.storage.batchdb;


public interface Reporter {
  static enum BatchDB { WRITE, COMPACT }

  public void progress() ;
  public void increment(String name, int amount) ;

  static public class LocalReporter implements Reporter {
    private String       name ;

    public LocalReporter(String name) {
      this.name = name ;
    }

    public void progress() { }

    public void increment(String name, int amount) {
    }
  }
}