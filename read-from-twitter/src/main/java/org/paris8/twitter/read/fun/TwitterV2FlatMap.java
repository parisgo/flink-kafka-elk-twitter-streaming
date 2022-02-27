package org.paris8.twitter.read.fun;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.paris8.twitter.read.bean.TwitterItem;

public class TwitterV2FlatMap implements FlatMapFunction<String, Tuple2<TwitterItem, String>> {
    private transient ObjectMapper jsonParser;

    @Override
    public void flatMap(String s, Collector<Tuple2<TwitterItem, String>> collector) throws Exception {
        if(StringUtils.isEmpty(s)) {
            return;
        }

        if(jsonParser == null) {
            jsonParser = new ObjectMapper();
        }

        try
        {
            JsonNode jsonNode = jsonParser.readValue(s, JsonNode.class);
            if(!jsonNode.has("data")) {
                return;
            }
            if(jsonNode.get("data").has("created_at") == false ||
                    jsonNode.get("data").has("lang") == false ||
                    jsonNode.get("data").has("text") == false) {
                return;
            }

            TwitterItem info = new TwitterItem(
                    jsonNode.get("data").get("created_at").asText(),
                    jsonNode.get("data").get("lang").asText(),
                    jsonNode.get("data").get("text").asText()
            );

            collector.collect(Tuple2.of(info, jsonParser.writeValueAsString(info)));
        }
        catch (Exception ex)
        {
            Log.error("TwitterV2FlatMap " + ex.getMessage());
        }
    }
}
