package fs.core

import fs.conf.Features
import util.{DataReader, SparkInit}

/**
 * Created by WanEnFu on 16/5/30.
 */
class FeatureSelection {

  def selectFeature(rankNum: Int, iterationNum: Int): List[String] = {
    println("// ========== Step.1 Spark Application ==========")
    val sc = SparkInit.getSparkContext()
    // ==============================================

    println("// ========== Step.2 Data RDD ==========")
    val featuresList = Features.featuresList

    // 取得 feature 並做成 RDD
    var data = new DataReader("/Users/WanEnFu/Desktop/small.csv")
      .readData()
      .selectFeatures(featuresList)
      .getLabelPoint()

    println("// ========== Repeat train SVM until rank satisfy ==========")
    var existFeatures = Array.fill[Int](featuresList.size+1)(1) // index:0 is label, then features.
    existFeatures(0) = 0 // label set 0.
    var result = List[(Double, (String, Int))]()
    while(rankNum <= existFeatures.sum) {

      println("// ========== Step.3 SVM ==========")
      val svm = new SVM(iterationNum)
      val model = svm.trainSVM(data)
      val ranks = svm.rank(existFeatures, model.weights.toArray)
      result = ranks._2
      existFeatures = ranks._1
      // ==============================================

      println("// ========== Step.4 Reget Data RDD ==========")
      val featureListWithLabel = ("label"::Features.featuresList).zip(existFeatures)
      val featureFilter = featureListWithLabel.filter(e => e._2 == 1).map(e => e._1)
      data = new DataReader("/Users/WanEnFu/Desktop/small.csv").readData().selectFeatures(featureFilter).getLabelPoint()
      // ==============================================
    }
    // ==============================================

    result.map(e => e._2._1)
  }
}
