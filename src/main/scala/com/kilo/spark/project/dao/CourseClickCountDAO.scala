package com.kilo.spark.project.dao

import com.kilo.spark.project.domain.CourseClickCount
import com.kilo.spark.project.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * 实战课程点击数
  * 数据访问层
  * Created by kilo on 2018/4/10.
  */
object CourseClickCountDAO {

  val tableName = "imooc_course_clickcount"
  val cf = "info"
  val qualifier = "click_count"

  /**
    * 保存数据到HBase
    *
    * @param list CourseClickCount集合
    */
  def save(list: ListBuffer[CourseClickCount]): Unit = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    for (ele <- list) {
      table.incrementColumnValue(ele.day_course.getBytes, cf.getBytes, qualifier.getBytes, ele.click_count)
    }
  }

  /**
    * 根据rowkey查询值
    *
    * @param day_course
    * @return
    */
  def count(day_course: String): Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    val get = new Get(day_course.getBytes())

    val value = table.get(get).getValue(cf.getBytes(), qualifier.getBytes())
    if (value == null) {
      0l
    } else {
      Bytes.toLong(value)
    }
  }


  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[CourseClickCount]
    list.append(CourseClickCount("20171111_8", 8))
    list.append(CourseClickCount("20171111_9", 9))
    list.append(CourseClickCount("20171111_1", 100))

//    save(list)

    println(count("20171111_8") + " : " + count("20171111_9") + " : " + count("20171111_1"))
  }
}
