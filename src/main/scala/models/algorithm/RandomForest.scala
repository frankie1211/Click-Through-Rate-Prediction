package models.algorithm

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.rdd.RDD
import util.DataReader2

/**
  * Created by benjamin658 on 2016/5/27.
  */
class RandomForestAlgorithm(trainData: RDD[LabeledPoint], testData: RDD[LabeledPoint]) {
  private val numClasses = 2
  private val categoricalFeaturesInfo = Map[Int, Int]()
  private val impurity = "gini"
  private val featureSubsetStrategy = "auto"

  def accurate(dataSet: RDD[LabeledPoint], model: RandomForestModel): (Double, Double, Double) = {
    val labelAndPreds = dataSet.map { point =>
      val score = model.trees.map(tree => tree.predict(point.features)).filter(_ > 0).size.toDouble / model.numTrees
      (score, point.label)
    }
    val metrics = new BinaryClassificationMetrics(labelAndPreds)
    val auROC = metrics.areaUnderROC
    val auPRC = metrics.areaUnderPR
    val correctNum = labelAndPreds.filter(pair => pair._1 != pair._2).count()

    (auROC, auPRC, correctNum.toDouble)
  }

  private def getCrossValidationData(): List[(RDD[LabeledPoint], RDD[LabeledPoint])] = {
    val test = for {i <- 1 to 10} yield {
      val Array(train, test) = trainData.randomSplit(Array(0.9, 0.1))
      (train, test)
    }

    test.toList
  }

  private def train(dataSet: RDD[LabeledPoint], maxTreeDepth: Int, maxBins: Int, numTrees: Int): RandomForestModel = {
    RandomForest.trainClassifier(
      dataSet,
      numClasses,
      categoricalFeaturesInfo,
      numTrees,
      featureSubsetStrategy,
      impurity,
      maxTreeDepth,
      maxBins)
  }

  def hyperParameterTuning(paramList: List[(Int, Int, Int)]): BestModel = {
    val crossData = getCrossValidationData()
    val modelList: List[List[((Double, Double, Double), (Int, Int, Int))]] = crossData.map(data => {
      paramList.map(params => {
        val maxTreeDepth = params._1
        val maxBins = params._2
        val numTrees = params._3
        val model = train(data._1, maxTreeDepth, maxBins, numTrees)

        (accurate(data._2, model), params)
      })
    }).transpose

    val sumList: List[(Double, Double, Double, (Int, Int, Int))] = modelList.map(model => {
      val length = model.size
      val avgROC = (model.map(_._1._1).sum) / length
      val avgPRC = (model.map(_._1._2).sum) / length
      val sumCorrectNum = (model.map(_._1._3).sum)
      (avgROC, avgPRC, sumCorrectNum, model(0)._2)
    })

    new BestModel(sumList.maxBy(_._3)._4)
  }

  class BestModel(bestParamList: (Int, Int, Int)) {
    def trainBestModel(dataSet: RDD[LabeledPoint]): RandomForestModel = {
      val maxTreeDepth = bestParamList._1
      val maxBins = bestParamList._2
      val numTrees = bestParamList._3
      train(dataSet, maxTreeDepth, maxBins, numTrees)
    }

    def getBestParmList(): (Int, Int, Int) = bestParamList
  }

}