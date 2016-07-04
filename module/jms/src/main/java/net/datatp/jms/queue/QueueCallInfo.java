package net.datatp.jms.queue;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class QueueCallInfo implements Serializable {
  private String componentId ;
  private String description ;
  private int    count ;
  private int    errorCount ;
  private LinkedList<String> messages = new LinkedList<String>() ;

  public QueueCallInfo() {} 

  public QueueCallInfo(String componentId, String desc) {
    this.componentId = componentId; 
    this.description = desc ;
  }

  public String getComponentId() { return componentId; }
  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }

  public String getDescription() { return description; }
  public void setDescription(String description) {
    this.description = description;
  }

  public int getCount() { return count; }
  public void setCount(int count) {
    this.count = count;
  }
  public void incrCount() { count++ ; }

  public int getErrorCount() { return errorCount; }
  public void setErrorCount(int errorCount) {
    this.errorCount = errorCount;
  }
  public void incrErrorCount() { errorCount++ ; }

  public List<String> getMessages() { return messages; }
  public void addMessage(String m) {
    messages.addFirst(m) ;
    if(messages.size() > 100) messages.removeLast() ;
  }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("Component Id: ").append(componentId).append("\n") ;
    b.append("Call Count:   ").append(count).append("\n") ;
    b.append("Call Error:   ").append(errorCount).append("\n") ;
    return b.toString() ;
  }
}