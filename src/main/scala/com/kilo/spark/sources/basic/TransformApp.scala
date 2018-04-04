package com.kilo.spark.sources.basic

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 黑名单过滤
  * Created by kilo on 2018/4/2.
  */
object TransformApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("TransformApp").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    //构建黑名单（应从数据库中读取）
    val blacks = List("zs", "ls")
    val blacksRDD = ssc.sparkContext.parallelize(blacks).map((_, true))

    //输入：20180402,zs
    val lines = ssc.socketTextStream("sparksql", 6789)

    //将输入与黑名单左关联，生成如(zs:[<20180402,zs>,<true>])的tuple
    val clicklog = lines.map(x => (x.split(",")(1), x)).transform(rdd => {
      rdd.leftOuterJoin(blacksRDD).filter(x => x._2._2.getOrElse(false) != true).map(x => x._2._1)
    })
    clicklog.print()


    ssc.start()
    ssc.awaitTermination()
  }
}
