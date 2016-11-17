package net.datatp.registry.event;

import java.util.Iterator;

import net.datatp.registry.Registry;

public class WaitingRandomNodeEventListener extends WaitingNodeEventListener {
  public WaitingRandomNodeEventListener(Registry registry) {
    super(registry);
  }
  
  synchronized protected void onDetectNodeEvent(NodeWatcher watcher, NodeEvent event) {
    Iterator<WaitingNodeEventWatcher> i = watcherQueue.iterator();
    while(i.hasNext()) {
      WaitingNodeEventWatcher waitingWatcher = i.next() ;
      if(waitingWatcher == watcher) {
        long time = System.currentTimeMillis() ;
        detectNodeEventCount++;
        eventLogs.add(new NodeEventLog(time - estimateLastDetectEventTime, event, watcher)) ;
        estimateLastDetectEventTime = time ;
        notifyAll();
        i.remove();
        return ;
      }
    }
  }
}