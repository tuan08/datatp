package net.datatp.util.stat;

import java.util.Iterator;
import java.util.TreeMap;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class Statistic extends TreeMap<String, StatisticEntry> {
  private String name ;

  public Statistic(String name) {
    this.name = name ;
  }

  public String getName() { return this.name ; }

  public void incr(String name, String relateTo, long amount) {
    if(name == null || name.length() == 0) name = "other" ;
    StatisticEntry value = get(name) ;
    if(value == null) put(name, new StatisticEntry(name, relateTo, amount)) ;
    else value.incr(amount) ;
  }

  public void incr(String[] name, long amount) {
    if(name == null || name.length == 0) {
      incr((String) null, null, amount) ;
      return ;
    } else {
      for(int i = 0; i < name.length; i++) {
        incr(name[i], null, amount) ;
      }
    }
  }

  public void incr(String[] name, String relateTo, long amount) {
    if(name == null || name.length == 0) {
      incr((String) null, relateTo, amount) ;
      return ;
    } else {
      for(int i = 0; i < name.length; i++) {
        incr(name[i], relateTo, amount) ;
      }
    }
  }

  public Object[] getModels() {
    Object[] models = new Object[size()] ;
    Iterator<StatisticEntry> i = values().iterator() ;
    int idx = 0 ;
    while(i.hasNext()) models[idx++] = i.next() ;
    return models ;
  }

  public void traverse(StatisticVisitor visitor) {
    Iterator<StatisticEntry> i = values().iterator() ;
    while(i.hasNext()) visitor.onVisit(this, i.next()) ;
  }
}