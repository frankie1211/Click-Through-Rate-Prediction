package util

/**
  * Created by WeiChen on 2016/5/24.
  */
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FunSuite

class SparkTest extends FunSuite {
  def localTest(name : String)(fun : SparkContext => Unit) : Unit = {
    this.test(name) {
      val conf = new SparkConf().setAppName(name).setMaster("local").set("spark.default.parallelism", "1")
      val sc = new SparkContext(conf)
      try {
        fun(sc)
      } finally {
        sc.stop()
      }
    }
  }
}