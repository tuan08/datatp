package net.datatp.flink.example.perftest;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import net.datatp.util.dataformat.DataSerializer;

public class PerfTest {
  
  private PerfTestConfig config;
  
  public PerfTest(PerfTestConfig config) {
    this.config = config;
  }
  
  public void run() throws Exception {
    //set up the execution environment
    StreamExecutionEnvironment env = null ;
    if(config.flinkJobManagerHost != null || config.flinkYarPropFile != null) {
      String   host        = config.flinkJobManagerHost;
      int      port        = config.flinkJobManagerPort;
      
      if(config.flinkYarPropFile != null) {
        Properties props = new Properties() ;
        props.load(new FileInputStream(config.flinkYarPropFile));;
        String connectUrl = props.getProperty("jobManager");
        if(connectUrl != null) {
          String[] pair = connectUrl.split(":") ;
          host = pair[0];
          port = Integer.parseInt(pair[1]);
        }
      }
      
      String[] jarFiles    = config.flinkJarFiles.split(",") ;
      int      parallelism = config.flinkParallelism;
      env = StreamExecutionEnvironment.createRemoteEnvironment(host, port, parallelism, jarFiles);
    } else {
      env = StreamExecutionEnvironment.getExecutionEnvironment();
      env.setParallelism(3);
    }
    
    KafkaMessageStreamFunction kafkaMessageStreamFunc = 
      new KafkaMessageStreamFunction("PerfTest", config.zkConnect, config.topicIn, Message.class) ;
    
    DataStream<Message> messageStream  = env.addSource(kafkaMessageStreamFunc);
    DataStream<Message> flattenStream = 
        messageStream.countWindowAll(config.flinkWindowSize, config.flinkWindowPeriod).apply(null);
    
    MessageOutputSelector outSelector = new MessageOutputSelector() ;
    
    SplitStream<Message> split = flattenStream.split(outSelector);
    for(int i = 0; i < config.numOPartition; i++) {
      DataStream<Message> partition  = split.select("partition-" + i);
      KafkaSinkFunction<Message> pSink = 
          new MessageKafkaSinkFunction("perftestOut", config.kafkaConnect, config.topicOut + ".p" + i) ;
      partition.addSink(pSink);
    }
    
    DataStream<Message> all = split.select("all");
    
    KafkaSinkFunction<Message> kafkaAllSink = 
      new MessageKafkaSinkFunction("perftestOut", config.kafkaConnect, config.topicOut) ;
    all.addSink(kafkaAllSink);

    //execute program
    env.execute("Perf Test");
  }
  
  static public class KafkaMessageStreamFunction extends KafkaStreamFunction<Message> {
    private static final long serialVersionUID = 1L;

    public KafkaMessageStreamFunction() {} 

    public KafkaMessageStreamFunction(String name, String zkConnect, String topic, Class<Message> type) {
      super("PerfTest", zkConnect, topic, type);
    }

    public void beforeCollect(Message mObj) {
      mObj.setStartDeliveryTime(System.currentTimeMillis());
    }
  }
  
  static public class MessageOutputSelector implements OutputSelector<Message> {
    private static final long serialVersionUID = 1L;

    @Override
    public Iterable<String> select(Message value) {
      ArrayList<String> names = new ArrayList<>();
      names.add(value.getPartition());
      names.add("all");
      return names;
    }
  };
    
  static public class MessageKafkaSinkFunction extends KafkaSinkFunction<Message> {
    private static final long serialVersionUID = 1L;
    
    public MessageKafkaSinkFunction() {} 
    
    public MessageKafkaSinkFunction(String name, String kafkaConnect, String topic) {
      super(name, kafkaConnect, topic);
    }
    
    public void invoke(Message message) throws Exception {
      message.setEndDeliveryTime(System.currentTimeMillis());
      super.invoke(message);
    }
  }
  
  public static void main(String[] args) throws Exception {
    long start = System.currentTimeMillis() ;
    PerfTestConfig config = new PerfTestConfig(args) ;
    
    System.out.println(DataSerializer.JSON.toString(config));
    
    KafkaMessageGenerator messageGenerator = 
      new KafkaMessageGenerator(config.kafkaConnect, config.topicIn, config.numOPartition, config.numOfMessagePerPartition);
    messageGenerator.setMessageSize(config.messageSize);
    messageGenerator.run();
    messageGenerator.waitForTermination(3600000);

    long generatorExecTime = System.currentTimeMillis() - start ;
    System.out.println("Message Generator Run In: " + generatorExecTime + "ms") ;
    
    start = System.currentTimeMillis() ;
    PerfTest perfTest = new PerfTest(config);
    perfTest.run();
    long perfTestExecTime = System.currentTimeMillis() - start ;
    System.out.println("PerfTest Run In: " + perfTestExecTime  + "ms") ;
    
    start = System.currentTimeMillis() ;
    KafkaMessageValidator validator =
      new KafkaMessageValidator(config.zkConnect, config.topicOut, 2, config.numOfMessagePerPartition);
    validator.run();
    validator.waitForTermination(3600000);
    
    System.out.println("Perf Test Generator Report:") ;
    System.out.println(messageGenerator.getTrackerReport()) ;
    
    System.out.println("Perf Test Validator Report:") ;
    System.out.println(validator.getTrackerReport()) ;
    long validatorExecTime = System.currentTimeMillis() - start ;
    
    System.out.println("PerfTest Run In: " + perfTestExecTime  + "ms") ;
    System.out.println("Message Generator Run In: " + generatorExecTime + "ms") ;
    System.out.println("Message Validator Run In: " + validatorExecTime + "ms");
  }
}
