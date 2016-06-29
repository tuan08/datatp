package net.datatp.flink.log4j;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import net.datatp.model.message.Message;

public class Log4joiner {
  
  public void run() throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(3);
    
    Log4jMessageGeneratorStreamFunction source1Func = new Log4jMessageGeneratorStreamFunction("Generator-1", 500, 250) ;
    DataStream<Message> mesg1Stream  = env.addSource(source1Func, "Log4j Generator 1");
    
    Log4jMessageGeneratorStreamFunction source2Func = new Log4jMessageGeneratorStreamFunction("Generator-2", 500, 250) ;
    DataStream<Message> mesg2Stream  = env.addSource(source2Func, "Log4j Generator 2");
    
    DataStream<Message>  mergeStream = mesg1Stream.union(mesg2Stream);
    mergeStream.print();
    //execute program
    env.execute("Perf Test");
  }
  
  public static void main(String[] args) throws Exception {
    Log4joiner perfTest = new Log4joiner();
    perfTest.run();
  }
}
