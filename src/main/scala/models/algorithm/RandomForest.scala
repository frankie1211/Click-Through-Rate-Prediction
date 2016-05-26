package models.algorithm

import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.tree.RandomForest
import util.DataReader

/**
  * Created by benjamin658 on 2016/5/27.
  */
object RandomForestModel {
  def main(args: Array[String]) {
    val targetFeatures = List(
      "banner_pos", "site_id", "site_category",
      "app_domain", "C1", "C14", "C15", "C16", "C17", "C18", "C19", "C20", "C21"
    )
    val data = new DataReader("/Users/benjamin658/workspace/develop/small.csv")
      .readData()
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
      val score = dtModel.trees.map(tree => tree.predict(point.features)).filter(_>0).size.toDouble/dtModel.numTrees
      (score, point.label)
    }

    val metrics = new BinaryClassificationMetrics(labelAndPreds)
    println("AUC Area : " + metrics.areaUnderROC())
  }
}
