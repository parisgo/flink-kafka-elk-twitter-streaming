# flink-kafka-elk-twitter-streaming
This is little project that i've used to test Flink, Kafka, ELK

## What it uses
- Flink 1.13.6
- Kafka 2.8.1
- ELK   7.13.2
- Twitter API v2
- Docker

## How to run the application

- modify `application.properties` file with your keys (place in `src/main/resources`):
```properties
    kafka.bootstrap.servers=kafka:9092
    kafka.topic=twitter-ukraine
    kafka.group.id=test
    
    twitter.bearer.token=<your key from twitter>
```
You need to create a Twitter developer application (https://developer.twitter.com) and get bearer token from your application's settings page

There are 2 ways to run this application, Intellij is by far the easiest that to run it

- start all services
  ![https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/01_docker.png?raw=true](https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/01_docker.png?raw=true)

- run application read-from-twitter
  ![https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/02_reader.png?raw=true](https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/02_reader.png?raw=true)

- run application write-to-es 
 ![https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/03_writer.png?raw=true](https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/03_writer.png?raw=true)

- check topic in Kafka
 ![https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/04_kafka.png?raw=true](https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/04_kafka.png?raw=true)

- show data in Kibana
- ![https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/05_kibana_discover.png?raw=true](https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/05_kibana_discover.png?raw=true)
- ![https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/06_kibana_dashboard.png?raw=true](https://github.com/parisgo/flink-kafka-elk-twitter-streaming/blob/main/doc/image/06_kibana_dashboard.png?raw=true)


