package net.datatp.wanalytic.flink;

import java.util.Arrays;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import net.datatp.model.message.Message;
import net.datatp.model.net.NetActivity;
import net.datatp.model.net.facebook.FBComment;
import net.datatp.model.net.facebook.FBPost;
import net.datatp.model.net.facebook.FBUser;

public class WAnalytic {
  private String   zkConnects        = "127.0.0.1:2181";
  private String[] esConnects        = { "127.0.0.1:9300" };
  private String   facebookFeedTopic = "facebook.crawler.data";

  public void runLocal() throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(3);
    run(env);
  }
  
  public void run(StreamExecutionEnvironment env) throws Exception {
    KafkaMessageStreamFunction fbSourceFunc = new KafkaMessageStreamFunction("fbfeed", zkConnects, facebookFeedTopic) ;
    DataStream<Message> fbDataStream  = env.addSource(fbSourceFunc, "fbfeed");
    
    DataStream<Message> dataStream = fbDataStream.flatMap(new  FBActivityExtractor());
    
    SplitStream<Message> split = dataStream.split(new FBSinkSelector());
    split.select("fb.user").addSink(new ESSinkFunction("fb.user", esConnects, "wa-fb-user", FBUser.class));
    split.select("fb.post").addSink(new ESSinkFunction("fb.post", esConnects, "wa-fb-post", FBPost.class));
    split.select("fb.comment").addSink(new ESSinkFunction("fb.comment", esConnects, "wa-fb-comment", FBComment.class));
    split.select("net.activity").addSink(new ESSinkFunction("net.activity", esConnects, "wa-net-activity", NetActivity.class));
    //execute program
    env.execute("WAnalytic");
  }
  
  static public class FBSinkSelector implements OutputSelector<Message> {
    private static final long serialVersionUID = 1L;

    @Override
    public Iterable<String> select(Message value) {
      return Arrays.asList(value.getType());
    }
  };
  
  public static class FBActivityExtractor implements FlatMapFunction<Message, Message> {
    private static final long serialVersionUID = 1L;
    
    @Override
    public void flatMap(Message message, Collector<Message> out) {
      out.collect(message);
      if("fb.post".equals(message.getType())) {
        FBPost post = message.getDataAs(FBPost.class);
        NetActivity activity = post.toNetActivity();
        out.collect(new Message(activity.getId(), activity, "net.activity"));
      } else if("fb.comment".equals(message.getType())) {
        FBComment comment = message.getDataAs(FBComment.class);
        NetActivity activity = comment.toNetActivity();
        out.collect(new Message(activity.getId(), activity, "net.activity"));
      }
    }
}
    
  public static void main(String[] args) throws Exception {
    WAnalytic wanalytic = new WAnalytic();
    wanalytic.runLocal();
  }
}
