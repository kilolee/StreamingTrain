package com.kilo.spark.kafka;

/**
 * Created by kilo on 2018/3/29.
 */
public class KafkaClientApp {
    public static void main(String[] args) {
        new KafkaProducer(KafkaProperties.TOPIC).start();
    }
}
