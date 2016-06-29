package net.datatp.model.net.facebook;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.datatp.model.net.NetActivity;

public class FBComment {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
  private Date                timestamp;
  
  private String id;
  private String fromId;
  private String forObjectId;
  private long   likeCount;
  private String message;

  public Date getTimestamp() { return timestamp; }
  public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getFromId() { return fromId; }
  public void setFromId(String fromId) { this.fromId = fromId; }
  
  public String getForObjectId() { return forObjectId; }
  public void setForObjectId(String forObjectId) { this.forObjectId = forObjectId; }
  
  public long getLikeCount() { return likeCount; }
  public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
  
  public void setLikeCount(Long likeCount) { 
    if(likeCount == null) return;
    this.likeCount = likeCount.longValue(); 
  }
  
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  
  public NetActivity toNetActivity() {
    NetActivity activity = new NetActivity();
    activity.setTimestamp(timestamp);
    activity.setId(id);
    activity.addCreatorId(fromId);
    activity.addTag("fb:comment");
    activity.addRefObjectId(forObjectId);
    activity.setContent(message);
    return activity;
  }
}
