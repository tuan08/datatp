package net.datatp.crawler.fetcher;

public class URLFetchQueueReport {
  private long inQueue;
  private long enqueue;
  private long inDelayQueue;
  private long enqueueDelay;
  
  public long getInQueue() { return inQueue; }
  public void setInQueue(long inQueue) { this.inQueue = inQueue; }
  
  public long getEnqueue() { return enqueue; }
  public void setEnqueue(long enqueue) { this.enqueue = enqueue;}
  
  public long getInDelayQueue() { return inDelayQueue; }
  public void setInDelayQueue(long inDelayQueue) { this.inDelayQueue = inDelayQueue; }
  
  public long getEnqueueDelay() { return enqueueDelay; }
  public void setEnqueueDelay(long enqueueDelay) { this.enqueueDelay = enqueueDelay; }

  
}