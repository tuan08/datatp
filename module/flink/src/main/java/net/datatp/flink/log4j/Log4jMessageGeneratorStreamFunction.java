package net.datatp.flink.log4j;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.GenericTypeInfo;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import net.datatp.model.message.Message;

public class Log4jMessageGeneratorStreamFunction extends RichSourceFunction<Message> implements ResultTypeQueryable<Message> {
  private static final long serialVersionUID = 1L;

  private String            sourceName;
  private int               numOfMessages;
  private int               rate = 1000;
  private Class<Message>    type             = Message.class;
  
  public Log4jMessageGeneratorStreamFunction() {} 
  
  public Log4jMessageGeneratorStreamFunction(String sourceName, int numOfMessages, int rate) {
    this.sourceName    = sourceName;
    this.numOfMessages = numOfMessages;
    this.rate = rate;
  } 
  
  @Override
  public TypeInformation<Message> getProducedType() { return new GenericTypeInfo<Message>(type); }
  
  @Override
  public void run(final SourceContext<Message> ctx) throws Exception {
    Log4jMessageGenerator generator = new  Log4jMessageGenerator(sourceName, numOfMessages);
    int count = 0;
    while(count < numOfMessages) {
      int limit = count + rate;
      if(limit > numOfMessages) limit = numOfMessages;
      long start = System.currentTimeMillis();
      while(count < limit) {
        Message message = generator.next();
        ctx.collect(message);
        count++;
      }
      
      long currTime = System.currentTimeMillis();
      if(currTime - start < 1000) {
        Thread.sleep(1000 - (currTime - start));
      }
    }
  } 
  
  @Override
  public void cancel() {
  }
}
