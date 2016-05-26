package models.algorithm

import models.core.LinearModel
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.regression.{GeneralizedLinearModel, LabeledPoint}
import org.apache.spark.rdd.RDD

/**
 * Created by WanEnFu on 16/5/26.
 */
class SVM extends LinearModel {

  override def hyperParameterTuning(data: RDD[LabeledPoint], test: RDD[LabeledPoint], iteration: List[Int], threshold: List[Double]): List[((Double, Double), GeneralizedLinearModel, (Int, Double))] = {
    val models = iteration.map(e => {
      val model = SVMWithSGD.train(data, e)
      val min = 1.0
      val max = 2.0

      (min, max, model, e)
    })

    val hyperList = models.map(e1 => {
      val hypers = threshold.map(e2 => {
        val resetModel = e1._3.setThreshold(e1._1 + (e1._2 - e1._1) * e2)
        (accurate(resetModel, test), resetModel, (e1._4, e2))
      })
      hypers
    })

    hyperList.flatten
  }
}