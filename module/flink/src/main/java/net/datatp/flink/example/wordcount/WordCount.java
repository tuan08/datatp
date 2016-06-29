package net.datatp.flink.example.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import net.datatp.util.log.LoggerFactory;

public class WordCount {
  public static void main(String[] args) throws Exception {
    LoggerFactory.log4jUseConsoleOutputConfig("INFO");

    final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(3);

    DataStream<String> text = env.fromElements(WordCountData.WORDS);
    DataStream<Tuple2<String, Integer>> counts = text.flatMap(new Tokenizer()).keyBy(0).sum(1);

    counts.print();
    env.execute("Streaming WordCount");
  }

  public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
    private static final long serialVersionUID = 1L;

    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
      // normalize and split the line
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
