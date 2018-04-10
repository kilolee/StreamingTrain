package com.kilo.spark.project.domain

/**
  * 实战课程点击数实体类
  *
  * @param day_course  对应的是HBase中的rowkey,20171111_1
  * @param click_count 对应的20171111_1的访问总数
  * Created by kilo on 2018/4/10.
  */
case class CourseClickCount(day_course: String, click_count: Long)
