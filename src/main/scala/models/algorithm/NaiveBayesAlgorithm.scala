package models.algorithm

import org.apache.spark.mllib.classification.{NaiveBayesModel, NaiveBayes}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by benjamin658 on 2016/6/7.
  */
class NaiveBayesAlgorithm(trainData: RDD[LabeledPoint], testData: RDD[LabeledPoint]) {

  def accurate(dataSet: RDD[LabeledPoint], model: NaiveBayesModel): (Double, Double, Double) = {
    val labelAndPreds = dataSet.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    val metrics = new BinaryClassificationMetrics(labelAndPreds)
    val auROC = metrics.areaUnderROC
    val auPRC = metrics.areaUnderPR
    val correctNum = labelAndPreds.filter(pair => pair._1 == pair._2).count()

    (auROC, auPRC, correctNum.toDouble)
  }

  private def getCrossValidationData(): List[(RDD[LabeledPoint], RDD[LabeledPoint])] = {
    val test = for {i <- 1 to 10} yield {
      val Array(train, test) = trainData.randomSplit(Array(0.9, 0.1))
      (train, test)
    }

    test.toList
  }

  private def train(dataSet: RDD[LabeledPoint], lambda: Double, modelType: String): NaiveBayesModel = {
    NaiveBayes.train(dataSet, lambda, modelType)
  }

  def hyperParameterTuning(paramList: List[(Double, String)]): BestModel = {
    val crossData = getCrossValidationData()
    val modelList: List[List[((Double, Double, Double), (Double, String))]] = crossData.map(data => {
      paramList.map(params => {
        val lambda = params._1
        val modelType = params._2
        val model = train(data._1, lambda, modelType)

        (accurate(data._2, model), params)
      })
    }).transpose

    val sumList: List[(Double, Double, Double, (Double, String))] = modelList.map(model => {
      val length = model.size
      val avgROC = (model.map(_._1._1).sum) / length
      val avgPRC = (model.map(_._1._2).sum) / length
      val sumCorrectNum = (model.map(_._1._3).sum)
      (avgROC, avgPRC, sumCorrectNum, model(0)._2)
    })

    new BestModel(sumList.maxBy(_._3))
  }

  class BestModel(bestParamList: (Double, Double, Double, (Double, String))) {
    def trainBestModel(dataSet: RDD[LabeledPoint]): NaiveBayesModel = {
      val lambda = bestParamList._1
      val modelType = bestParamList._4._2
      train(dataSet, lambda, modelType)
    }

    def getBestParmList(): (Double, Double, Double, (Double, String)) = bestParamList
  }

}

