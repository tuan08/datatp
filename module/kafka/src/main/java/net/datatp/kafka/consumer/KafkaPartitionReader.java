package net.datatp.kafka.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import kafka.cluster.BrokerEndPoint;
import kafka.javaapi.PartitionMetadata;
import net.datatp.kafka.KafkaTool;
import net.datatp.util.JSONSerializer;


public class KafkaPartitionReader {
  final static public int DEFAULT_FETCH_SIZE = 512 * 1024;
  private String   name;
  private KafkaTool kafkaClient ;
  private String topic ;
  private TopicPartition topicPartition;
  private PartitionMetadata partitionMetadata;
  private int fetchSize = DEFAULT_FETCH_SIZE;
  private KafkaConsumer<String, byte[]> consumer;
  private long currentOffset = 0;
  
  private List<ConsumerRecord<String, byte[]>>     currentRecordSet;
  private Iterator<ConsumerRecord<String, byte[]>> currentRecordSetIterator;
  private boolean closed = false;
  
  public KafkaPartitionReader(String name, KafkaTool kafkaClient, String topic, PartitionMetadata pMetadata) throws Exception {
    this.name = name;
    this.kafkaClient = kafkaClient;
    this.topic = topic;
    this.topicPartition = new TopicPartition(topic, pMetadata.partitionId());
    this.partitionMetadata = pMetadata;
    reconnect() ;
    currentOffset = getLastCommitOffset();
  }
  
  public int getPartition() { return partitionMetadata.partitionId(); }
  
  public long getCurrentOffset() { return this.currentOffset ; }
  
  public void setFetchSize(int size) { this.fetchSize = size; }
  
  public void reconnect() throws Exception {
    if(consumer != null) consumer.close();
    BrokerEndPoint broker = partitionMetadata.leader();
    if(broker != null) {
      Properties props = new Properties();
      props.put("bootstrap.servers", broker.host() + ":" + broker.port());
      props.put("group.id", name);
      props.put("enable.auto.commit", "false");
      props.put("auto.commit.interval.ms", "1000");
      props.put("session.timeout.ms", "30000");
      props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
      props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
      consumer = new KafkaConsumer<>(props);
      consumer.assign(Arrays.asList(topicPartition));
    } else {
      reconnect(3, 5000);
    }
  }
  
  public void reconnect(int retry, long retryDelay) throws Exception {
    if(consumer != null) consumer.close();
    Exception error = null ;
    for(int i = 0; i < retry; i++) {
      Thread.sleep(retryDelay);
      //Refresh the partition metadata
      try {
        partitionMetadata = kafkaClient.findPartitionMetadata(topic, partitionMetadata.partitionId());
        BrokerEndPoint broker = partitionMetadata.leader();
        if(broker != null) {
          Properties props = new Properties();
          props.put("bootstrap.servers", broker.host() + ":" + broker.port());
          props.put("group.id", name);
          props.put("enable.auto.commit", "false");
          props.put("auto.commit.interval.ms", "1000");
          props.put("session.timeout.ms", "30000");
          props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
          props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
          consumer = new KafkaConsumer<>(props);
          consumer.assign(Arrays.asList(topicPartition));
          return;
        }
      } catch(Exception ex) {
        error = ex ;
      }
    }
    throw new Exception("Cannot connect after " + retry + " times", error);
  }
  
  synchronized public void commit() throws Exception {
    CommitOperation commitOp = new CommitOperation(currentOffset) ;
    execute(commitOp, 5, 5000);
  }
  
  public void rollback() throws Exception  {
    currentOffset = getLastCommitOffset();
    currentRecordSet = null ;
    currentRecordSetIterator = null;
  }
  
  synchronized public void close() throws Exception {
    System.out.println("close partition reader " + topicPartition.partition());
    consumer.close();
    closed = true;
  }
  
  public byte[] next(long maxWait) throws Exception {
    ConsumerRecord<String, byte[]> messageAndOffset = nextMessageAndOffset(maxWait);
    if(messageAndOffset == null) return null ;
    return messageAndOffset.value();
  }

  public ConsumerRecord<String, byte[]> nextMessageAndOffset(long maxWait) throws Exception {
    if(currentRecordSetIterator == null) nextMessageSet(maxWait);
    if(currentRecordSetIterator.hasNext()) {
      ConsumerRecord<String, byte[]> sel = currentRecordSetIterator.next();
      currentOffset = sel.offset() + 1;
      return sel;
    }
    nextMessageSet(maxWait);
    if(currentRecordSetIterator.hasNext()) {
      ConsumerRecord<String, byte[]> sel = currentRecordSetIterator.next();
      currentOffset = sel.offset() + 1;
      return sel;
    }
    return null;
  }
  
  public <T> T nextAs(Class<T> type, long maxWait) throws Exception {
    byte[] data = next(maxWait);
    if(data == null) return null;
    return JSONSerializer.INSTANCE.fromBytes(data, type);
  }

  public List<ConsumerRecord<String, byte[]>> fetch(int fetchSize, int maxRead, long maxWait) throws Exception {
    return fetch(fetchSize, maxRead, maxWait, 5) ;
  }
  
  synchronized public List<ConsumerRecord<String, byte[]>> fetch(int fetchSize, int maxRead, long maxWait, int numRetries) throws Exception {
    FetchMessageOperation fetchOperation = new FetchMessageOperation(fetchSize, maxRead, (int)maxWait);
    List<ConsumerRecord<String, byte[]>> records = execute(fetchOperation, numRetries, 5000);
    currentOffset += records.size();
    return records;
  }
 
  void nextMessageSet(long maxWait) throws Exception {
    currentRecordSet = fetch(fetchSize, 1000, maxWait, 5);
    currentRecordSetIterator = currentRecordSet.iterator();
  }
  
  long getLastCommitOffset() {
    OffsetAndMetadata offsetAndMetadata = consumer.committed(topicPartition);
    if(offsetAndMetadata == null) return 0l;
    return offsetAndMetadata.offset();
  }
  
  <T> T execute(Operation<T> op, int retry, long retryDelay) throws Exception {
    Exception error = null;
    for(int i = 0; i < retry; i++) {
      try {
        if(closed) {
          error = new Exception("The reader has been closed");
          break;
        }
        if(error != null) reconnect(1, retryDelay);
        return op.execute();
      } catch(Exception ex) {
        error = ex;
      }
    }
    throw new Exception("Cannot fetch the data after retry =" + retry + ", retry delay = " + retryDelay, error);
  }
  
  static public interface Operation<T> {
    public T execute() throws Exception ;
  }
  
  class CommitOperation implements Operation<Boolean> {
    long offset;
    
    public CommitOperation(long offset) {
      this.offset = offset;
    }
    
    @Override
    public Boolean execute() throws Exception {
      OffsetAndMetadata offsetAndMeta = new OffsetAndMetadata(offset, "");
      Map<TopicPartition, OffsetAndMetadata> mapForCommitOffset = new HashMap<TopicPartition, OffsetAndMetadata>();
      mapForCommitOffset.put(topicPartition, offsetAndMeta);
      consumer.commitSync(mapForCommitOffset);
      return true;
    }
  }

  class FetchMessageOperation implements Operation<List<ConsumerRecord<String, byte[]>>> {
    int fetchSize;
    int maxRead;
    int maxWait;
    
    public FetchMessageOperation(int fetchSize, int maxRead, int maxWait) {
      this.fetchSize = fetchSize;
      this.maxRead = maxRead ;
      this.maxWait = maxWait ;
    }
    
    public List<ConsumerRecord<String, byte[]>> execute() throws Exception {
      consumer.seek(topicPartition, currentOffset);
      ConsumerRecords<String, byte[]> records = consumer.poll(maxWait);
      List<ConsumerRecord<String, byte[]>> recordList = records.records(topicPartition);
      List<ConsumerRecord<String, byte[]>> holder = new ArrayList<ConsumerRecord<String, byte[]>>();
      int count = 0;
      for(ConsumerRecord<String, byte[]> record : recordList) {
        if(record.offset() < currentOffset) continue; //old offset, ignore
        holder.add(record);
        count++;
        if(count == maxRead) break;
      }
      return holder ;
    }
  }
}