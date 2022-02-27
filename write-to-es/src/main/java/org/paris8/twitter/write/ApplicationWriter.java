package org.paris8.twitter.write;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.paris8.twitter.write.config.PropertiesConstants;

import java.util.ArrayList;
import java.util.Properties;

public class ApplicationWriter {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", PropertiesConstants.current().get(PropertiesConstants.BOOTSTRAP_SERVERS));
        properties.setProperty("group.id", PropertiesConstants.current().get(PropertiesConstants.KAFKA_GROUP_ID));
        String topic = PropertiesConstants.current().get(PropertiesConstants.KAFKA_TOPIC);

        //read data from Kafka
        FlinkKafkaConsumer<String> myConsumer = new FlinkKafkaConsumer<String>(topic, new SimpleStringSchema(), properties);
        myConsumer.setStartFromLatest();

        DataStream<String> dataStream = env.addSource(myConsumer);
        env.enableCheckpointing(5000);

        //write to ES
        ArrayList<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost(PropertiesConstants.current().get(PropertiesConstants.ES_SERVER), new Integer(PropertiesConstants.current().get(PropertiesConstants.ES_PORT))));
        dataStream.addSink(new ElasticsearchSink.Builder<String>(httpHosts, new MyEsSinkFunction()).build());

        env.execute("Twitter writer");
    }

    public static class MyEsSinkFunction implements ElasticsearchSinkFunction<String> {
        @Override
        public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
            IndexRequest source = new IndexRequest(PropertiesConstants.current().get(PropertiesConstants.ES_INDEX)).source(element, XContentType.JSON);
            indexer.add(source);
        }
    }
}
