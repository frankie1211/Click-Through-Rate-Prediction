package models

import models.algorithm.LogisticRegression
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by WeiChen on 2016/5/26.
  */
object Main {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("test").setMaster("local").set("spark.default.parallelism", "1")
    val sc = new SparkContext(conf)
    val data = RDD[LabeledPoint]
    val test = RDD[LabeledPoint]
    val hyperParameters = List(Map("threshold" -> 0.1), Map("threshold" -> 0.2))
    val lr = new LogisticRegression
    val best = lr.hyperParameterTuning(data, test, hyperParameters)

  }
}
