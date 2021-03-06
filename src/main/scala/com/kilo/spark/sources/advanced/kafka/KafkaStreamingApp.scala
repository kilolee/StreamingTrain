package com.kilo.spark.sources.advanced.kafka

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Spark Streaming 从Kafka中接收数据，采用基于Receiver的方式
  * Created by kilo on 2018/4/9.
  */
object KafkaStreamingApp {
  def main(args: Array[String]): Unit = {

    if (args.length != 4) {
      System.err.println("Usage:KafkaStreamingApp <zkQuorum><group><topics><numTreads>")
      System.exit(1)
    }
    val Array(zkQuorum, group, topics, numTreads) = args

    val sparkConf = new SparkConf().setAppName("KafkaReceiverWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val topicMap = topics.split(",").map((_, numTreads.toInt)).toMap
    //Spark Streaming对接Kafka
    val kafkaStream = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)

    //DStream of (Kafka message key, Kafka message value),所以是_._2
    kafkaStream.map(_._2).flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).print()

    ssc.start()
    ssc.awaitTermination()
  }

}
