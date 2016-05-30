package models.algorithm

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.tree.RandomForest
import util.DataReader2

/**
  * Created by benjamin658 on 2016/5/27.
  */
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
      .randomSplit(Array(0.8, 0.2))

    val trainData = data(0)
    val testData = data(1)

    println("開始訓練模型.....")
    val maxTreeDepth = 30
    val maxBins = 100
    val numClasses = 2
    val numTrees = 256
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val featureSubsetStrategy = "auto"

    val dtModel = RandomForest.trainClassifier(trainData, numClasses, categoricalFeaturesInfo, numTrees, featureSubsetStrategy, impurity, maxTreeDepth, maxBins)
    println("模型訓練完成.....")

    println("計算精準度.....")
    val labelAndPreds = testData.map { point =>
      val score = dtModel.trees.map(tree => tree.predict(point.features)).filter(_ > 0).size.toDouble / dtModel.numTrees
      (score, point.label)
    }


    val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
    val accur = 1 - testErr

    //    println("Train Length : " + trainData.collect().length)
    //    println("Test Length : " + testData.collect().length)
    println("精準度 = " + accur)

    val metrics = new BinaryClassificationMetrics(labelAndPreds)
    println("AUC Area : " + metrics.areaUnderROC())
  }
}
