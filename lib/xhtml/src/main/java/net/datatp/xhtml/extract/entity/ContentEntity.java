package net.datatp.xhtml.extract.entity;

import java.io.Serializable;

import net.datatp.util.text.StringUtil;

public class ContentEntity extends ExtractEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  private String   title;
  private String   description;
  private String   content;
  
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  
  @Override
  String getFormattedText() {
    StringBuilder b = new StringBuilder();
    b.append("Type:  ").append(getType()).append("\n");
    b.append("Tags:  ").append(StringUtil.joinStringArray(getTags())).append("\n");
    b.append("Title: ").append(title).append("\n");
    b.append("Description: ").append(description).append("\n");
    b.append("Content: \n");
    b.append(content).append("\n");
    return b.toString();
  }
}
