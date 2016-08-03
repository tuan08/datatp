package net.datatp.xhtml.extract.entity;

import java.io.Serializable;

import net.datatp.util.text.StringUtil;

public class CommentEntity extends ExtractEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  private String[] comment;
  
  public CommentEntity() {}
  
  public CommentEntity(String ... comment) {
    this.comment = comment;
  }
  
  public String[] getComment() { return comment; }
  public void setComment(String ... comment) { this.comment = comment; }

  @Override
  String getFormattedText() {
    StringBuilder b = new StringBuilder();
    b.append("Type: ").append(getType()).append("\n");
    b.append("Tags: ").append(StringUtil.joinStringArray(getTags())).append("\n");
    b.append("Comment: \n");
    if(comment != null) {
      for(String sel : comment) {
        b.append(sel).append("\n");
      }
    }
    return b.toString();
  }
}
