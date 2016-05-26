package models.core

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by WeiChen on 2016/5/26.
  */
trait SmallModel {
  def train(data:RDD[LabeledPoint])

  def accurate()
}
