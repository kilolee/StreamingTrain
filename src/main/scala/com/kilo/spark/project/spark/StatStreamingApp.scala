package com.kilo.spark.project.spark

import com.kilo.spark.project.domain.ClickLog
import com.kilo.spark.project.utils.DateUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 使用Spark Streaming处理Kafka过来的数据
  * Created by kilo on 2018/4/10.
  */
object StatStreamingApp {
  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      System.err.println("Usage:StatStreamingApp <zkQuorum><group><topics><numThreads>")
      System.exit(1)
    }

    val Array(zkQuorum, group, topics, numThreads) = args

    val sparkConf = new SparkConf().setAppName("StatStreamingApp").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val messages = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)

    //    messages.map(_._2).print()

    val logs = messages.map(_._2)
    val cleanData = logs.map(line => {
      val infos = line.split("\t")

      // infos(2) = "GET /class/130.html HTTP/1.1"
      // url = /class/130.html
      val url = infos(2).split(" ")(1)

      var courseId = 0
      if (url.startsWith("/class")) {
        val courseIdHTML = url.split("/")(2)
        courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf(".")) toInt
        //此种写法错误，报数组越界错误
        // courseId = courseIdHTML.split(".")(0).toInt
      }
      ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))
    }).filter(clickLog => clickLog.courseId != 0)

    cleanData.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
