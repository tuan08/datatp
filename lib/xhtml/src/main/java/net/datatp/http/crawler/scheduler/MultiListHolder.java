package net.datatp.http.crawler.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author : Tuan Nguyen
 *          tuan08@gmail.com
 * Jul 2, 2010  
 */
public class MultiListHolder<T> {
  private int capacity  ;
  private int currentSize ;
  private Map<String, LinkedList<T>> lists = new HashMap<String, LinkedList<T>>() ;
  
  public MultiListHolder(int capacity) {
    this.capacity = capacity ;
  }
  
  public boolean isFull() { return currentSize >= capacity ; }
  
  public int getNumberOfList() { return this.lists.size() ; }
  
  public int getListSize(String name) {
    LinkedList<T> list = lists.get(name) ;
    if(list == null) return -1; 
    return list.size() ;
  }
  
  public int getCurrentSize() { return this.currentSize ; }
  
  public int getCapacity() { return this.capacity ; }
  
  public void assertEmpty() {
    if(lists.size() > 0 || currentSize > 0) {
      throw new RuntimeException("Buffer is not empty") ;
    }
  }
  
  public void add(String listName, T datum) {
    if(currentSize >= capacity) {
      throw new RuntimeException("Buffer is full, Cannot store more than " + capacity) ;
    }
    
    LinkedList<T> holder = lists.get(listName) ;
    if(holder == null) {
      holder = new LinkedList<T>() ;
      lists.put(listName, holder) ;
    }
    holder.add(datum) ;
    currentSize++ ;
  }

  public RandomBlockIterator getRandomBlockIterator(int blockSize) { return new RandomBlockIterator(blockSize) ; }
  
  public RandomIterator getRandomIterator() { return new RandomIterator() ; }
  
  public class RandomBlockIterator {
    int blockSize ;
    Iterator<LinkedList<T>> iterator ;
    
    public RandomBlockIterator(int size) { this.blockSize = size ; }
    
    public List<T> next() {
      if(iterator == null || !iterator.hasNext()) {
        if(lists.size() == 0) return null ;
        iterator = lists.values().iterator() ;
      }
      LinkedList<T> holder = iterator.next() ;
      List<T> block = new ArrayList<T>(blockSize) ;
      int limit = blockSize ;
      if(limit > holder.size()) limit = holder.size() ;
      for(int i = 0; i < limit; i++)  {
        if(i % 2 == 0) block.add(holder.removeFirst()) ;
        else  block.add(holder.removeLast()) ;
      }
      
      if(holder.size() == 0) iterator.remove() ;
      currentSize -= block.size() ;
      return block ;
    }
  }
  
  public class RandomIterator {
    Iterator<LinkedList<T>> iterator ;
    
    public T next() {
      if(iterator == null || !iterator.hasNext()) {
        if(lists.size() == 0) return null ;
        iterator = lists.values().iterator() ;
      }
      LinkedList<T> holder = iterator.next() ;
      T ret = null ;
      if(Math.random() < 0.5) ret = holder.removeFirst() ;
      else  ret = holder.removeLast() ;
      if(holder.size() == 0) iterator.remove() ;
      currentSize -- ;
      return ret ;
    }
  }
}