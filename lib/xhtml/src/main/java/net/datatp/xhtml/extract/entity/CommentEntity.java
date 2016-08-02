package net.datatp.xhtml.extract.entity;

import java.io.Serializable;

public class CommentEntity extends ExtractEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  private String[] comment;
  
  public CommentEntity() {}
  
  public CommentEntity(String ... comment) {
    this.comment = comment;
  }
  
  public String[] getComment() { return comment; }
  public void setComment(String ... comment) { this.comment = comment; }
}
