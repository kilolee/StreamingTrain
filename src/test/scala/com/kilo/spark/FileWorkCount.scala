package com.kilo.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 使用Spark Streaming 处理文件系统（local/hdfs）的数据
  * Created by kilo on 2018/4/2.
  */
object FileWorkCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("FileWordCount").setMaster("local")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val lines = ssc.textFileStream("file:///G:/ss/")
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    result.print()

    ssc.start()
    ssc.awaitTermination()
  }

}
