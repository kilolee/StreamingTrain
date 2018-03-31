package com.kilo.spark.kafka;


import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * kafka消费者
 * Created by kilo on 2018/3/30.
 */
public class KafkaConsumer extends Thread {

    private String topic;

    public KafkaConsumer(String topic) {
        this.topic = topic;
    }

    private ConsumerConnector createConnector() {
        Properties properties = new Properties();
        properties.setProperty("zookeeper.connect", KafkaProperties.ZK);
        properties.setProperty("group.id", KafkaProperties.GROUP_ID);
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }

    @Override
    public void run() {
        ConsumerConnector consumer = createConnector();

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        //"1"说明list中只有一个成员
        topicCountMap.put(topic, 1);

        //String：topic
        //List<KafkaStream<byte[], byte[]> 对应的数据流
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStream = consumer.createMessageStreams(topicCountMap);

        //获取每次收到的数据
        KafkaStream<byte[], byte[]> stream = messageStream.get(topic).get(0);

        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();

        while (iterator.hasNext()) {
            String message = new String(iterator.next().message());
            System.out.println("rec:" + message);
        }

    }
}
