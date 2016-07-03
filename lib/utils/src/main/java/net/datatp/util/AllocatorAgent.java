package net.datatp.util;

public interface AllocatorAgent<T extends Comparable<T>> {
  public T next() ;
  public T[] next(T[] holder) ;
  public T nextExclude(T[] excludes) ;
  public T[] getItems() ;
  public int available() ;
  public boolean hasItem(T object) ;
  
  static public class RoundRobin<T extends Comparable<T>> implements AllocatorAgent<T> {
    private Iterator<T>   iterator ;
    
    public RoundRobin(T[] items) {
      this.iterator = new Iterator<T>(items) ;
    }
    
    public T next() { return iterator.next() ; }

    public T[] next(T[] holder) {
      if(holder.length > iterator.items.length) {
        throw new RuntimeException("The request item is bigger than the available items") ;
      }
      for(int i = 0; i < holder.length; i++) holder[i] = iterator.next() ;
      return holder ;
    }
    
    public T nextExclude(T[] excludes) {
      if(excludes.length >= iterator.items.length) {
        throw new RuntimeException("The exclude set item is bigger than the available items") ;
      }
      while(true) {
        T ret = iterator.next() ; 
        if(!isIn(ret, excludes)) return ret ;
      }
    }
    
    public T[] getItems() { return this.iterator.items ; }
    
    public int available() { return this.iterator.items.length ; }
    
    public boolean hasItem(T object) {
      return isIn(object, iterator.items) ;
    }
    
    private boolean isIn(T object, T[] objects) {
      if(objects == null) return false ;
      for(int i = 0; i < objects.length; i++) {
        if(object.compareTo(objects[i]) == 0) return true ;
      }
      return false ;
    }
    
    static public class Iterator<T> {
      T[] items ;
      int pos ;
      
      Iterator(T[] available) {
        this.items = available ;
        this.pos = 0 ;
      }
      
      public T next() {
        T ret = items[pos++] ;
        if(pos == this.items.length) pos = 0;
        return ret ;
      }
    }
  }
  
}