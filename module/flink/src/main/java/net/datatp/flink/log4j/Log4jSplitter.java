package net.datatp.flink.log4j;

import java.util.ArrayList;

import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import net.datatp.util.log.Log4jRecord;
import net.datatp.model.message.Message;

public class Log4jSplitter {
  
  public void run() throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(3);
    
    Log4jMessageGeneratorStreamFunction sourceFunc = new Log4jMessageGeneratorStreamFunction("Generator", 1000, 250) ;
    
    DataStream<Message> mesgStream  = env.addSource(sourceFunc, "Log4j Generator");
    
    MessageOutputSelector outSelector = new MessageOutputSelector() ;
    
    SplitStream<Message> split = mesgStream.split(outSelector);
    String[] logLevel = { "INFO", "WARNING", "ERROR" } ;
    for(int i = 0; i < logLevel.length; i++) {
      DataStream<Message> partition  = split.select(logLevel[i]);
      Log4jMessageSinkFunction pSink = new Log4jMessageSinkFunction() ;
      partition.addSink(pSink);
    }
    //execute program
    env.execute("Perf Test");
  }
  
  static public class MessageOutputSelector implements OutputSelector<Message> {
    private static final long serialVersionUID = 1L;

    @Override
    public Iterable<String> select(Message value) {
      ArrayList<String> names = new ArrayList<>();
      Log4jRecord logRec = value.getDataAs(Log4jRecord.class);
      names.add(logRec.getLevel());
      return names;
    }
  };
    
  public static void main(String[] args) throws Exception {
    Log4jSplitter perfTest = new Log4jSplitter();
    perfTest.run();
  }
}
