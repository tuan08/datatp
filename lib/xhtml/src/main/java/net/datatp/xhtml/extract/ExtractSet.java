package net.datatp.xhtml.extract;

import java.util.List;

public class ExtractSet {
  private String name;
  private String description;
  
  private List<Extract> extracts;

  public String getName() { return name; }
  public void setName(String name) { this.name = name;}

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public List<Extract> getExtracts() { return extracts; }
  public void setExtracts(List<Extract> extracts) { this.extracts = extracts; }
}
