package net.datatp.xhtml.extract.entity;

import java.io.Serializable;

public class PostEntity extends ExtractEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  private String   title;
  private String[] post;
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String[] getPost() { return post; }
  public void setPost(String[] post) { this.post = post; }
}