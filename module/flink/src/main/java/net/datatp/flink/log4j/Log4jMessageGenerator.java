package net.datatp.flink.log4j;

import java.util.concurrent.atomic.AtomicInteger;

import net.datatp.util.log.Log4jRecord;
import net.datatp.model.message.Message;
import net.datatp.model.message.MessageTracking;

public class Log4jMessageGenerator {
  private String        sourceName;
  private AtomicInteger idTracker = new AtomicInteger();
  private int           numOfMessages = 100;

  public Log4jMessageGenerator(String sourceName, int numOfMessages) {
    this.sourceName = sourceName;
    this.numOfMessages = numOfMessages;
  }
  
  public Message next() {
    if(idTracker.get() == numOfMessages) return null;
    long start = System.currentTimeMillis();
    int trackId = idTracker.getAndIncrement();
    String key = sourceName + "-" + trackId;
    Log4jRecord logRec = new Log4jRecord();
    
    if(trackId % 3 == 1)      logRec.setLevel("WARNING");
    else if(trackId % 3 == 2) logRec.setLevel("ERROR");
    else                      logRec.setLevel("INFO");
    logRec.setHost("localhost");
    logRec.setLoggerName("Generator");
    logRec.setMessage("This is a message with key " + key);
    
    Message message = new Message(key, logRec);
    MessageTracking msgMracking = new MessageTracking(sourceName, trackId);
    msgMracking.addTimeLog("generator", start, System.currentTimeMillis());
    message.setMessageTracking(msgMracking);
    return message;
  }
}
