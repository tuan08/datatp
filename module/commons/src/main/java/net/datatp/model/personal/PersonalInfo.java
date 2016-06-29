package net.datatp.model.personal;

import java.util.Date;

public class PersonalInfo {
  private String id;
  
  private String fullName;
  private String firstName;
  private String lastName;
  private Date   birthday;
  private String religion;
  private String gender;
  private String locale;
  
  private String about;
  private String bio ;
  
  public PersonalInfo() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public Date getBirthday() { return birthday; }
  public void setBirthday(Date birthday) { this.birthday = birthday; }

  public String getReligion() { return religion; }
  public void setReligion(String religion) { this.religion = religion; }

  public String getGender() { return gender; }
  public void setGender(String gender) { this.gender = gender; }

  public String getLocale() { return locale; }
  public void setLocale(String locale) { this.locale = locale; }
  
  public String getAbout() { return about; }
  public void setAbout(String about) { this.about = about; }

  public String getBio() { return bio; }
  public void setBio(String bio) { this.bio = bio; }
}
