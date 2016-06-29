package net.datatp.model.net.facebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.datatp.model.location.Location;
import net.datatp.model.personal.PersonalInfo;

public class FBUser {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
  private Date                timestamp;
  
  private PersonalInfo personalInfo;
  private Location     location;
  
  public FBUser() {}

  public Date getTimestamp() { return timestamp; }
  public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

  public PersonalInfo getPersonalInfo() { return personalInfo; }
  public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }

  public Location getLocation() { return location; }
  public void setLocation(Location location) { this.location = location; }
}
