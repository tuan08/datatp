package net.datatp.wanalytic.facebook;

import java.util.ArrayList;
import java.util.List;

public class FBObjectIdQueue {
  private List<String> objectIds ;
  private int currIdx;
  
  public FBObjectIdQueue(List<String> objectIds) {
    this.objectIds = objectIds;
  }
  
  synchronized String nextId() {
    if(currIdx >= objectIds.size()) return null;
    return objectIds.get(currIdx++);
  }
  
  synchronized List<String> nextIdChunk(int size) {
    if(currIdx >= objectIds.size()) return null;
    List<String> holder = new ArrayList<String>();
    int limit = currIdx + size;
    if(limit > objectIds.size()) limit = objectIds.size();
    while(currIdx < limit) {
      holder.add(objectIds.get(currIdx++));
    }
    return holder;
  }
}
