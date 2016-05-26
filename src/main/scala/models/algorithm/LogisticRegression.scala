package models.algorithm

import models.core.LinearModel
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.{GeneralizedLinearModel, LabeledPoint}
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS

/**
  * Created by WeiChen on 2016/5/26.
  */
class LogisticRegression extends LinearModel {
  override def hyperParameterTuning(data: RDD[LabeledPoint],test:RDD[LabeledPoint], iteration: List[Integer] = List(10, 100, 1000), threshold: List[Double]): (GeneralizedLinearModel, (Double, Double)) = {
    val model = new LogisticRegressionWithLBFGS().setNumClasses(2).run(data)
    val hyper = threshold.map { t =>
      val acc = accurate(model.clearThreshold().setThreshold(t), test)
      (model, acc)
    }
    hyper.maxBy(_._2._1)
  }
}