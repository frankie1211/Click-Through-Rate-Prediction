package models

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by WeiChen on 2016/5/26.
  */
object Main {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("test").setMaster("local").set("spark.default.parallelism", "1")
    val sc = new SparkContext(conf)
  }
}
