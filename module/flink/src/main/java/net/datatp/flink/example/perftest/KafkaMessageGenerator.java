package net.datatp.flink.example.perftest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.datatp.kafka.producer.DefaultKafkaWriter;

public class KafkaMessageGenerator {
  private String               kafkaConnect;
  private String               topic;
  private int                  numOfExecutor           = 1;
  private int                  numOfMessagePerExecutor = 100;
  private int                  messageSize             = 128;
  private ExecutorService      executorService;
  private BitSetMessageTracker generatorTracker;
  private AtomicLong           counter = new AtomicLong();
  
  public KafkaMessageGenerator(String kafkaConnect, String topic, int numOfExecutor, int numOfMessagePerExecutor) {
    this.kafkaConnect = kafkaConnect;
    this.topic = topic;
    this.numOfExecutor = numOfExecutor;
    this.numOfMessagePerExecutor = numOfMessagePerExecutor;
  }
  
  public KafkaMessageGenerator setMessageSize(int size) {
    this.messageSize = size;
    return this ;
  }
  
  public void run() throws Exception {
    generatorTracker = new BitSetMessageTracker(numOfMessagePerExecutor) ;
    executorService = Executors.newFixedThreadPool(numOfExecutor);
    for(int i = 0; i < numOfExecutor; i++) {
      executorService.submit(new MessageGeneratorExecutor("partition-" + i));
    }
    executorService.shutdown();
  }
  
  public void waitForTermination(long maxTimeout) throws InterruptedException {
    executorService.awaitTermination(maxTimeout, TimeUnit.MILLISECONDS);
  }
  
  public String getTrackerReport() {
    return generatorTracker.getFormatedReport();
  }
  
  public class MessageGeneratorExecutor implements Runnable {
    private String partition ;
    
    MessageGeneratorExecutor(String partition) {
      this.partition = partition ;
    }
    
    @Override
    public void run() {
      try {
        execute() ;
      } catch(Throwable e) {
        e.printStackTrace();
      }
    }
    
    private void execute() throws Exception {
      DefaultKafkaWriter writer = new DefaultKafkaWriter("LogSample", kafkaConnect);
      for(int i = 0; i < numOfMessagePerExecutor; i++) {
        String key = "partition="+ partition + ", key=" + i;
        Message message = new Message(partition, i, key, new byte[messageSize]) ;
        message.setStartDeliveryTime(System.currentTimeMillis());
        writer.send(topic, message, 5000);
        generatorTracker.log(partition, i);
        long count = counter.incrementAndGet();
        if(count % 10000 == 0) {
          System.out.println("Message Generator Progress " + count + " messages");
        }
      }
      writer.close();
      System.out.println("Message Generator Progress " + counter.get() + " messages");
    }
  }
}
