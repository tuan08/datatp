package net.datatp.tracking.kafka;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import net.datatp.kafka.KafkaTool;
import net.datatp.kafka.consumer.KafkaPartitionReader;
import net.datatp.tracking.TrackingMessage;
import net.datatp.tracking.TrackingReader;
import net.datatp.tracking.TrackingRegistry;

public class KafkaTrackingReader extends TrackingReader {
  @Parameter(names = "--zk-connect", description = "The zk connect string")
  private String zkConnects = "localhost:2181";
  
  @Parameter(names = "--topic", description = "The topic")
  private String topic = "tracking";
  
  @Parameter(names = "--max-message-wait-time", description = "The max message wait time")
  private long maxMessageWaitTime = 5000;
  
  final AtomicInteger accCommit = new AtomicInteger();

  private KafkaPartitionReader[]       partitionReader;
  private KafkaPartitionReaderWorker[] partitionReaderWorker;

  private BlockingQueue<KafkaPartitionReader> readerQueue = new LinkedBlockingQueue<>();
  private ExecutorService executorService;
  private BlockingQueue<TrackingMessage> queue = new LinkedBlockingQueue<>(5000);
  
  public KafkaTrackingReader(String[] args) throws Exception {
    new JCommander(this, args);
  }
  
  public void onInit(TrackingRegistry registry) throws Exception {
    KafkaTool kafkaTool = new KafkaTool("KafkaTool", zkConnects);
    TopicMetadata topicMeta = kafkaTool.findTopicMetadata(topic);
    List<PartitionMetadata> partitionMetas = topicMeta.partitionsMetadata();
    int numOfPartitions = partitionMetas.size();
    partitionReader = new KafkaPartitionReader[numOfPartitions];
    for (int i = 0; i < numOfPartitions; i++) {
      partitionReader[i] = new KafkaPartitionReader("VMTMValidatorKafkaApp", kafkaTool, topic, partitionMetas.get(i));
      readerQueue.offer(partitionReader[i]);
    }
    kafkaTool.close();
    
    int NUM_OF_THREAD = 3;
    partitionReaderWorker = new KafkaPartitionReaderWorker[NUM_OF_THREAD];
    executorService = Executors.newFixedThreadPool(partitionReaderWorker.length);
    for(int i = 0; i < partitionReaderWorker.length; i++) {
      partitionReaderWorker[i] = new KafkaPartitionReaderWorker(); 
      executorService.submit(partitionReaderWorker[i]);
    }
    executorService.shutdown();
  }
  
  public void onTrackingMessage(TrackingMessage tMesg) throws Exception {
    queue.offer(tMesg, maxMessageWaitTime, TimeUnit.MILLISECONDS);
  }
 
  public void shutdown() throws Exception {
    executorService.shutdownNow();
    for(int i = 0; i < partitionReaderWorker.length; i++) {
      partitionReaderWorker[i].terminate = true;
    }
    executorService.awaitTermination(15000, TimeUnit.MILLISECONDS);

    for(int i = 0; i < partitionReader.length; i++) {
      partitionReader[i].close();
    }
  }
  
  
  public void onDestroy(TrackingRegistry registry) throws Exception{
    shutdown();
  }

  @Override
  public TrackingMessage next() throws Exception {
    try {
      return queue.poll(maxMessageWaitTime, TimeUnit.MILLISECONDS);
    } catch(InterruptedException ex) {
      return null;
    }
  }
  
  public class KafkaPartitionReaderWorker implements Runnable {
    boolean terminate = false;
    
    public void run() {
      try {
        while(!terminate) {
          KafkaPartitionReader pReader = readerQueue.take();
          TrackingMessage tMesg = null;
          int count = 0;
          while(!terminate && (tMesg = pReader.nextAs(TrackingMessage.class, 250)) != null) {
            onTrackingMessage(tMesg);
            count++;
            if(count == 5000) break;
          }
          accCommit.addAndGet(count);
          pReader.commit();
          readerQueue.offer(pReader);
        }
      } catch (InterruptedException e) {
        System.err.println("KafkaTrackingReader:  KafkaPartitionReader is interruptted" );
      } catch (Throwable e) {
        System.err.println("Fail to load the data from kafka");
        e.printStackTrace();
      }
    }
  }
}