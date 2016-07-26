package net.datatp.crawler.urldb;

public interface URLDatumFactory {
  static public URLDatumFactory DEFAULT = new URLDatumFactory() {
    @Override
    public URLDatum createInstance() { return new URLDatum(); }

    @Override
    public URLDatum createInstance(long timestamp) { return new URLDatum(timestamp); }
  };
  
  public URLDatum createInstance();
  public URLDatum createInstance(long timestamp);
}
