package net.datatp.model.net.facebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.datatp.model.net.NetActivity;

public class FBPost {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
  private Date                timestamp;
  
  private String id;
  private String fromId;
  private String type;
  private String message;
  private int    likeCount;
  private int    commentCount;
  
  public Date getTimestamp() { return timestamp; }
  public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getFromId() { return fromId; }
  public void setFromId(String fromId) { this.fromId = fromId; }
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  
  public int getLikeCount() { return likeCount; }
  public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
  
  public int getCommentCount() { return commentCount; }
  public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
  
  
  public NetActivity toNetActivity() {
    NetActivity activity = new NetActivity();
    activity.setTimestamp(timestamp);
    activity.setId(id);
    activity.addCreatorId(fromId);
    activity.addTag("fb:post");
    activity.setContent(type + " - " + message);
    return activity;
  }
}
