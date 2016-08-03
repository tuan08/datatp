package net.datatp.xhtml.extract.entity;

import java.io.Serializable;

import net.datatp.util.text.StringUtil;

public class PostEntity extends ExtractEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  private String   title;
  private String[] post;
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String[] getPost() { return post; }
  public void setPost(String[] post) { this.post = post; }
  
  @Override
  String getFormattedText() {
    StringBuilder b = new StringBuilder();
    b.append("Type:  ").append(getType()).append("\n");
    b.append("Tags:  ").append(StringUtil.joinStringArray(getTags())).append("\n");
    b.append("Title: ").append(title).append("\n");
    b.append("Post: \n");
    if(post != null) {
      for(String sel : post) {
        b.append(sel).append("\n");
      }
    }
    return b.toString();
  }
}