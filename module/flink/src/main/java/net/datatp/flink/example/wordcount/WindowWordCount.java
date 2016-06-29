package net.datatp.flink.example.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem.WriteMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author tuan
 */
public class WindowWordCount {
  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(3);

    DataStream<String> text = env.fromElements(WordCountData.WORDS);
    DataStream<Tuple2<String, Integer>> counts =
      text.flatMap(new Tokenizer()).keyBy(0).
      //timeWindow(Time.milliseconds(50)).
      countWindow(1000 /*window*/, 1 /*slideSize*/).
      sum(1);
    counts.writeAsText("build/wordcounts", WriteMode.OVERWRITE);
    counts.print();
    env.execute("WindowWordCount");
  }
  
  static public class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
    private static final long serialVersionUID = 1L;

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
      // normalize and split the line
      System.out.println("split : " + value);
      String[] tokens = value.toLowerCase().split("\\W+");

      // emit the pairs
      for (String token : tokens) {
        if (token.length() > 0) {
          out.collect(new Tuple2<String, Integer>(token, 1));
        }
      }
    }
  }
}
