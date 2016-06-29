package net.datatp.flink.example.perftest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import net.datatp.kafka.consumer.KafkaMessageConsumerConnector;
import net.datatp.kafka.consumer.MessageConsumerHandler;
import net.datatp.util.JSONSerializer;

public class KafkaMessageValidator {
  private String                        zkConnect;
  private String                        topic;
  private int                           numOfExecutor                  = 1;
  private int                           expectNumOfMessagePerPartition = 100;
  private long                          consumerTimeout = 10000;
  private KafkaMessageConsumerConnector kafkaConnector;
  private BitSetMessageTracker          messageTracker;
  private AtomicLong sumDeliveryTime = new AtomicLong();
  private AtomicLong messageCount    = new AtomicLong();

  public KafkaMessageValidator(String zkConnect, String topic, int numOfExecutor, int expectNumOfMessagePerPartition) {
    this.zkConnect = zkConnect;
    this.topic = topic;
    this.numOfExecutor = numOfExecutor;
    this.expectNumOfMessagePerPartition = expectNumOfMessagePerPartition;
  }
  
  public void setConsumerTimeout(long timeout) {
    this.consumerTimeout = timeout;
  }
  
  public void run() throws Exception {
    messageTracker = new BitSetMessageTracker(expectNumOfMessagePerPartition) ;
    kafkaConnector = 
        new KafkaMessageConsumerConnector("KafkaMessageValidator", zkConnect).
        withConsumerTimeoutMs(consumerTimeout).
        connect();
   
    sumDeliveryTime = new AtomicLong();
    messageCount = new AtomicLong();
    MessageConsumerHandler handler = new MessageConsumerHandler() {
      @Override
      public void onMessage(String topic, byte[] key, byte[] message) {
        Message mObj = JSONSerializer.INSTANCE.fromBytes(message, Message.class);
        messageTracker.log(mObj.getPartition(), mObj.getTrackId());
        long count = messageCount.incrementAndGet();
        long deliveryTime = mObj.getEndDeliveryTime() - mObj.getStartDeliveryTime();
        sumDeliveryTime.addAndGet(deliveryTime);
        if(count % 10000 == 0) {
          System.out.println("Message Validator Progress " + count + " messages");
        }
      }
    };
    kafkaConnector.consume(topic, handler, numOfExecutor);
  }
  
  public void waitForTermination(long maxTimeout) throws InterruptedException {
    kafkaConnector.awaitTermination(maxTimeout, TimeUnit.MILLISECONDS);
    System.out.println("Message Validator Progress " + messageCount.get() + " messages");
  }
  
  public String getTrackerReport() {
    StringBuilder b = new StringBuilder() ;
    b.append(messageTracker.getFormatedReport());
    b.append("\n\n");
    long count = messageCount.get() > 0 ? messageCount.get() : 1l ;
    b.append("Avg Delivery Time: " + (sumDeliveryTime.get()/count) + "ms");
    return b.toString();
  }
}
