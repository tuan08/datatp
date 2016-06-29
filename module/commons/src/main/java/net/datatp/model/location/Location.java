package net.datatp.model.location;

public class Location {
  private String id;
  private String type;
  private String name;

  private double longitude;
  private double latitude;
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
  public String getName() { return name; }
  
  public void setName(String name) { this.name = name; }
  
  public double getLongitude() { return longitude; }
  public void setLongitude(double longitude) { this.longitude = longitude; }
  
  public double getLatitude() { return latitude;}
  public void setLatitude(double latitude) { this.latitude = latitude;}
}
