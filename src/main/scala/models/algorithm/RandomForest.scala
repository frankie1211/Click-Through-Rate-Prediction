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

  private def accurate(dataSet: RDD[LabeledPoint], model: RandomForestModel): Double = {
    val labelAndPreds = dataSet.map { point =>
      val score = model.trees.map(tree => tree.predict(point.features)).filter(_ > 0).size.toDouble / model.numTrees
      (score, point.label)
    }

    new BinaryClassificationMetrics(labelAndPreds).areaUnderROC()
  }

  private def getCrossValidationData(): List[(RDD[LabeledPoint], RDD[LabeledPoint])] = {
    val test = for {i <- 1 to 10} yield {
      val Array(train, test) = trainData.randomSplit(Array(0.9, 0.1))
      (train, test)
    }

    test.toList
  }

  private def train(dataSet: RDD[LabeledPoint], maxTreeDepth: Int, maxBins: Int, numTrees: Int): RandomForestModel = {
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val featureSubsetStrategy = "auto"

    RandomForest.trainClassifier(dataSet, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxTreeDepth, maxBins)
  }

  def hyperParameterTuning(paramList: List[(Int, Int, Int)]): FinalModel = {
    val crossData = getCrossValidationData()
    val modelList = paramList.map(params => {
      val maxTreeDepth = params._1
      val maxBins = params._2
      val numTrees = params._3
      crossData.map(data => {
        val model = train(data._1, maxTreeDepth, maxBins, numTrees)
        val modelAccurate = accurate(data._2, model)
        (model, modelAccurate)
      })
    }).flatten

    new FinalModel(modelList)
  }

  class FinalModel(modelList: List[(RandomForestModel, Double)]) {
    def getBestModel(): (RandomForestModel, Double) = {
      modelList.maxBy(_._2)
    }
  }

}

object Demo {
  def main(args: Array[String]) {
    val targetFeatures = List(
      "banner_pos", "site_id", "hour",
      "C17", "C21", "C19", "C20", "C18", "C1"
    )
    println("#####################Start to load data########################")
    val data = new DataReader2().chain
      .readFile("/Users/benjamin658/workspace/develop/mid.csv")
      .selectFeatures(targetFeatures)
      .getLabelPoint()
      .randomSplit(Array(0.8, 0.2))
    val trainData = data(0)
    val testData = data(1)

    println("#####################Start to train model########################")
    val rdf = new RandomForestAlgorithm(trainData, testData)
    val model = rdf.hyperParameterTuning(List((20, 200, 350))).getBestModel()
    println("#####################Train model finish########################")
    println("AUC Area : " + model._2)
  }
}