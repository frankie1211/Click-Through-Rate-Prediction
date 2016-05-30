package models.algorithm

import models.core.TreeModel
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.rdd.RDD
import util.DataReader2

/**
  * Created by benjamin658 on 2016/5/27.
  */
class Rdf extends TreeModel {
  def train(data: RDD[LabeledPoint]): RandomForestModel = {

  }

  def accurate(model: RandomForestModel, test: RDD[LabeledPoint]): (Double, Double) = {

  }
}

object RandomForestModel {
  def main(args: Array[String]) {
    val targetFeatures = List(
      "banner_pos", "site_id", "hour",
      "C17", "C21", "C19", "C20", "C18", "C1"
    )
    // "C14", "C15", "C16", "C17", "C18", "C19", "C20","site_category","app_domain",
    val data = new DataReader2().chain
      .readFile("/Users/benjamin658/workspace/develop/mid.csv")
      .selectFeatures(targetFeatures)
      .getLabelPoint()
      .randomSplit(Array(0.6, 0.4))

    val trainData = data(0)
    val testData = data(1)

    println("開始訓練模型.....")
    val maxTreeDepth = 5
    val maxBins = 32
    val numClasses = 2
    val numTrees = 64
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val featureSubsetStrategy = "auto"

    val dtModel = RandomForest.trainClassifier(trainData, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxTreeDepth, maxBins)
    println("模型訓練完成.....")

    println("計算精準度.....")
    val labelAndPreds = testData.map { point =>
      val prediction = dtModel.predict(point.features)
      (point.label, prediction)
    }

    val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
    val accur = 1 - testErr

    //    println("Train Length : " + trainData.collect().length)
    //    println("Test Length : " + testData.collect().length)
    println("精準度 = " + accur)
  }
}
