# flink-kafka-elk-twitter-streaming
This is little project that i've used to test Flink, Kafka, ELK

##What it uses
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
You need to create a Twitter developer application and get bearer token from your application's settings page

There are 2 ways to run this application, Intellij is by far the easiest that to run it

- start all services 

- run application read-from-twitter 

- run application write-to-es 

