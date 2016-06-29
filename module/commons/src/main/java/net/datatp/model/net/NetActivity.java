package net.datatp.model.net;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NetActivity {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
  private Date                timestamp;
  
  private String      id;
  private Set<String> creatorIds;
  private Set<String> refObjectIds;
  private String      description;
  private String      content;
  private Set<String> tags;

  public Date getTimestamp() { return timestamp; }
  public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public Set<String> getCreatorIds() { return creatorIds; }
  public void setCreatorIds(Set<String> creatorIds) { this.creatorIds = creatorIds; }

  public void addCreatorId(String id) {
    if(creatorIds == null) creatorIds = new HashSet<>();
    creatorIds.add(id);
  }
  
  public Set<String> getRefObjectIds() { return refObjectIds;}
  public void setRefObjectIds(Set<String> refObjectIds) { this.refObjectIds = refObjectIds; }
  
  public void addRefObjectId(String id) {
    if(refObjectIds == null) refObjectIds = new HashSet<>();
    refObjectIds.add(id);
  }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  
  public Set<String> getTags() { return tags; }
  public void setTags(Set<String> tags) { this.tags = tags; }

  public void addTag(String tag) {
    if(tags == null) tags = new HashSet<>();
    tags.add(tag);
  }
}
