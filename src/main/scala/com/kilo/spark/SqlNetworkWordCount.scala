package com.kilo.spark

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}

/**
  * Spark Streaming整合Spark SQL 完成词频统计操作
  * Created by kilo on 2018/4/2.
  */
object SqlNetworkWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("SqlNetworkWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val lines = ssc.socketTextStream("sparksql", 6789)
    val words = lines.flatMap(_.split(" "))

    //将DStream类型的words转换为DataFrame,运行SQL查询
    words.foreachRDD((rdd: RDD[String], time: Time) => {
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._

      //RDD => DataFrame
      val wordsDataFrame = rdd.map(w => Record(w)).toDF()

      wordsDataFrame.createOrReplaceTempView("words")

      val wordCountsDataFrame = spark.sql("select word,count(*) as total from words group by word")
      println(s"==============$time===============")
      wordCountsDataFrame.show()
    })

    ssc.start()
    ssc.awaitTermination()
  }

  case class Record(word: String)

  /**
    * 单例模式获取SparkSession实例
    */
  object SparkSessionSingleton {
    @transient
    private var instance: SparkSession = _

    def getInstance(sparkConf: SparkConf): SparkSession = {
      if (instance == null) {
        instance = SparkSession.builder().config(sparkConf).getOrCreate()
      }
      instance
    }
  }

}
