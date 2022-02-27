package org.paris8.twitter.read.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.utils.ParameterTool;

@Slf4j
public class PropertiesConstants {
    public static final String BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    public static final String KAFKA_TOPIC       = "kafka.topic";
    public static final String KAFKA_GROUP_ID    = "kafka.group.id";

    public static final String TWITTER_BEARER_TOKEN = "twitter.bearer.token";

    private PropertiesConstants() {
    }

    public static ParameterTool current() {
        try {
            return ParameterTool.fromPropertiesFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            log.error("PropertiesConstants error: " + e.getMessage());
        }

        return ParameterTool.fromSystemProperties();
    }
}
