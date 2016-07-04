package net.datatp.queue;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface DataQueue <T> {
  boolean offer(T obj, long timeout, TimeUnit unit) throws InterruptedException ;
  boolean offer(T[] obj, long timeout, TimeUnit unit) throws InterruptedException ;
  boolean offer(List<T> list, long timeout, TimeUnit unit) throws InterruptedException ;

  public T poll(long timeout, TimeUnit unit) throws InterruptedException;
  public int drainTo(Collection<T> c, int maxElements, long timeout, TimeUnit unit) throws InterruptedException;
}
