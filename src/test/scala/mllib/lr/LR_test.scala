package mllib.lr
import org.apache.log4j.{Level, LogManager}
import org.apache.spark.rdd.RDD
import util.SparkTest;
/**
  * Created by WeiChen on 2016/5/24.
  */
class LR_test extends SparkTest{
  localTest("Spark Context creating test") { sc =>
    LogManager.getRootLogger.setLevel(Level.ERROR)
    val rdd = sc.parallelize(Array(("1", "msg_one"), ("2", "msg_two")))
    val res = rdd.count()
    val expected = 2
    assertResult(expected)(res)
  }
}
