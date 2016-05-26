package models.algorithm

import models.core.LinearModel
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.{GeneralizedLinearModel, LabeledPoint}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD
import models.hyperparameter.LogisticRegressionArgs

/**
  * Created by WeiChen on 2016/5/26.
  */
class LogisticRegression[D] extends LinearModel[D] {
  override def train(data: RDD[LabeledPoint], hp: D): GeneralizedLinearModel = ???
}