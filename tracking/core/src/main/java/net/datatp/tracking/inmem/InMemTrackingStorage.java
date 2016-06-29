package net.datatp.tracking.inmem;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.datatp.tracking.TrackingMessage;
import net.datatp.tracking.TrackingReader;
import net.datatp.tracking.TrackingStorage;
import net.datatp.tracking.TrackingWriter;

public class InMemTrackingStorage implements TrackingStorage {
  private BlockingQueue<TrackingMessage> queue = new LinkedBlockingQueue<>(1000);
  
  public void offer(TrackingMessage msg, long timeout) throws InterruptedException {
    queue.offer(msg, timeout, TimeUnit.MILLISECONDS);
  }
  
  public TrackingMessage poll(long timeout) throws InterruptedException {
    TrackingMessage trackingMessage =  queue.poll(timeout, TimeUnit.MILLISECONDS);
    return trackingMessage;
  }
  
  static public class Writer extends TrackingWriter {
    private InMemTrackingStorage db;
    
    public Writer(InMemTrackingStorage db) {
      this.db = db;
    }
    
    @Override
    public void write(TrackingMessage message) throws Exception {
      db.offer(message, 10000);
    }
  }
  
  static public class Reader extends TrackingReader {
    private InMemTrackingStorage db;
    
    public Reader(InMemTrackingStorage db) {
      this.db = db;
    }

    @Override
    public TrackingMessage next() throws Exception {
      return db.poll(10000);
    }
    
  }
}
