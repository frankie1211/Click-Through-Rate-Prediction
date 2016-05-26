package models.algorithm

import models.core.LinearModel
import org.apache.spark.mllib.regression.{GeneralizedLinearModel, LabeledPoint}
import org.apache.spark.rdd.RDD

/**
  * Created by WeiChen on 2016/5/26.
  */
class LogisticRegression extends LinearModel{
  override def train(data: RDD[LabeledPoint]): GeneralizedLinearModel = ???

  override def accurate(): Unit = ???
}
