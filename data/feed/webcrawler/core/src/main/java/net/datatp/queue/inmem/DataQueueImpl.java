package net.datatp.queue.inmem;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.queue.DataQueue;

public class DataQueueImpl<T> implements DataQueue<T> {
  private BlockingQueue<T> queue = new  LinkedBlockingQueue<>();
  
  
  @Override
  public boolean offer(T obj, long timeout, TimeUnit unit) throws InterruptedException {
    return queue.offer(obj, timeout, unit);
  }

  @Override
  public boolean offer(T[] obj, long timeout, TimeUnit unit) throws InterruptedException {
    boolean result = true;
    for(T selObj : obj) {
      if(!queue.offer(selObj, timeout, unit)) result = false;
    }
    return result;
  }

  @Override
  public boolean offer(List<T> list, long timeout, TimeUnit unit) throws InterruptedException {
    boolean result = true;
    for(int i = 0; i < list.size(); i++) {
      T selObj = list.get(i);
      if(!queue.offer(selObj, timeout, unit)) result = false;
    }
    return result;
  }

  @Override
  public T poll(long timeout, TimeUnit unit) throws InterruptedException {
    return queue.poll(timeout, unit);
  }

  @Override
  public int drainTo(Collection<T> c, int maxElements, long timeout, TimeUnit unit) throws InterruptedException {
    return queue.drainTo(c, maxElements);
  }
}
