package org.paris8.twitter.read.fun;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.paris8.twitter.read.config.PropertiesConstants;

import java.io.IOException;

@Slf4j
public class TwitterSourceV2 extends RichSourceFunction<String> {
    private Boolean running = true;
    private static final OkHttpClient client = new OkHttpClient();
    private static final String BEARER_TOKEN = PropertiesConstants.current().get(PropertiesConstants.TWITTER_BEARER_TOKEN);
    private Call call = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        String ruleJson = "{ \"add\": [ { \"value\": \"ukraine\", \"tag\": \"ukraine\"}] }";
        RequestBody bodyRule = RequestBody.create(ruleJson, MediaType.parse("application/json"));

        Request ruleRequest = new Request.Builder()
                .url("https://api.twitter.com/2/tweets/search/stream/rules")
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .post(bodyRule)
                .build();

        try {
            Response ruleResponse = client.newCall(ruleRequest).execute();

            if(ruleResponse.code() == 201) {
                Request queryRequest = new Request.Builder().get()
                        .url("https://api.twitter.com/2/tweets/search/stream?tweet.fields=lang,created_at")
                        .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                        .build();

                call = client.newCall(queryRequest);
            }
        } catch (Exception e) {
            running = false;
            log.error(e.getMessage());
        }
    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        if(!running || call == null) {
            return;
        }

        try (BufferedSource responseBody = call.execute().body().source()) {
            while (!responseBody.exhausted()) {
                String body = responseBody.readUtf8Line();
                if(!StringUtils.isEmpty(body)) {
                    ctx.collect(body);
                }
            }
        } catch (IOException e) {
            running = false;
            log.error("TwitterSource " + e.getMessage());
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
