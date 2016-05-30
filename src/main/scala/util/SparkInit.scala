package util

/**
  * Created by benjamin658 on 2016/5/26.
  */

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object SparkInit {
  val appName = "CTR"
  val master = "local[*]"
  val conf = new SparkConf()
    .setAppName(appName)
    .setMaster(master)
    .set("spark.io.compression.codec", "org.apache.spark.io.LZ4CompressionCodec")

  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)


  def getSparkContext(): SparkContext = {
    sc
  }

  def getSqlContext(): SQLContext = {
    sqlContext
  }
}
