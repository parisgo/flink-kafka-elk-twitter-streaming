package org.paris8.twitter.read;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.paris8.twitter.read.bean.ItemCount;
import org.paris8.twitter.read.bean.TwitterItem;
import org.paris8.twitter.read.config.PropertiesConstants;
import org.paris8.twitter.read.fun.*;

import java.time.Duration;
import java.util.Properties;

public class ApplicationReader {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> dataStream = env.addSource(new TwitterSourceV2());
        DataStream<Tuple2<TwitterItem, String>> twitterStream = dataStream.flatMap(new TwitterV2FlatMap());

        DataStream<TwitterItem> twitterWindow = twitterStream.map(new MapFunction<Tuple2<TwitterItem, String>, TwitterItem>() {
            @Override
            public TwitterItem map(Tuple2<TwitterItem, String> tuple) throws Exception {
                return tuple.f0;
            }
        }).assignTimestampsAndWatermarks(
            WatermarkStrategy.<TwitterItem>forBoundedOutOfOrderness(Duration.ofSeconds(5)).withTimestampAssigner((event, timestamp)-> event.getTimestamp())
        );

        DataStream<ItemCount> twitterWindowAgg = twitterWindow.keyBy(TwitterItem::getLang)
                .window(SlidingEventTimeWindows.of(Time.minutes(5), Time.seconds(10)))
                .allowedLateness(Time.minutes(1))
                .aggregate(new ItemCountAgg(), new ItemCountResult());

        DataStream<String> twitterResult = twitterWindowAgg
                .keyBy(ItemCount::getWindowEnd)
                .process(new ItemOrderList());
        twitterResult.print();

        //write data to Kafka
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesConstants.current().get(PropertiesConstants.BOOTSTRAP_SERVERS));

        FlinkKafkaProducer<String> twitterProducer = new FlinkKafkaProducer<>(
                PropertiesConstants.current().get(PropertiesConstants.KAFKA_TOPIC),
                new SimpleStringSchema(),
                properties);

        twitterStream.map(new MapFunction<Tuple2<TwitterItem, String>, String>() {
            @Override
            public String map(Tuple2<TwitterItem, String> tuple) throws Exception {
                return tuple.f1;
            }
        }).addSink(twitterProducer);

        env.execute("Twitter reader");
    }
}
