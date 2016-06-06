package models

import models.algorithm.{RandomForestAlgorithm, SVM, LogisticRegression}
import org.apache.log4j.LogManager
import org.apache.log4j.Level
import org.apache.spark.mllib.classification.{SVMWithSGD, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import util.DataReader2
import vote.ModelVote


/**
  * Created by WeiChen on 2016/5/26.
  */
object Main {
  def getValidationData(data: RDD[LabeledPoint]): List[(RDD[LabeledPoint], RDD[LabeledPoint])] = {
    val test = for {i <- 1 to 10} yield {
      val Array(train, test) = data.randomSplit(Array(0.9, 0.1))
      (train, test)
    }
    test.toList
  }

  def main(args: Array[String]) {
    val targetFeatures = List(
      "banner_pos", "site_id", "hour",
      "C17", "C21", "C19", "C20", "C18", "C1"
    )

    println("#####################Start to load data########################")
    val Array(trainData, testData) = new DataReader2().chain
      .readFile("/Users/benjamin658/workspace/develop/mid.csv")
      .selectFeatures(targetFeatures)
      .getLabelPoint()
      .randomSplit(Array(0.8, 0.2))

    val splitData = getValidationData(trainData)
    LogManager.getRootLogger.setLevel(Level.ERROR)

    println("##################### Start to train model ########################")

    println("##################### First Model : Logistic Regression ########################")
    val lr = new LogisticRegression
    val lrModels = lr.hyperParameterTuning(splitData, List(0), List(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9))
    val bestParameter = lr.findBestModel(lrModels)
    val lrModel = new LogisticRegressionWithLBFGS().setNumClasses(2).run(trainData)
    val bestLRModel = lrModel.clearThreshold().setThreshold(bestParameter._4._2)
    println("##################### First Model Done ########################")

    println("##################### Second Model : SVM ########################")
    val svm = new SVM
    val svmModels = svm.hyperParameterTuning(splitData, List(10, 20), List(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
    val bestPar = svm.findBestModel(svmModels)
    val svmModel = SVMWithSGD.train(trainData, bestPar._4._1)
    svmModel.clearThreshold()

    val scoreAndLabels = trainData.map { point =>
      val score = svmModel.predict(point.features)
      (score, point.label)
    }

    val min = scoreAndLabels.min()(new Ordering[Tuple2[Double, Double]]() {
      override def compare(x: (Double, Double), y: (Double, Double)): Int =
        Ordering[Double].compare(x._1, y._1)
    })

    val max = scoreAndLabels.max()(new Ordering[Tuple2[Double, Double]]() {
      override def compare(x: (Double, Double), y: (Double, Double)): Int =
        Ordering[Double].compare(x._1, y._1)
    })

    val bestSVMModel = svmModel.clearThreshold().setThreshold(min._1 + (max._1 - min._1) * bestPar._4._2)
    println("##################### Second Model Done ########################")

    println("##################### Third Model : Random Forest ########################")
    val rdf = new RandomForestAlgorithm(trainData, testData)
    val bestRandomForestModel = rdf.hyperParameterTuning(List((10, 20, 50), (20, 30, 100))).trainBestModel(trainData)
    println("##################### Third Model Done ########################")

    println("##################### Start to vote ########################")
    val modelVote = new ModelVote(bestLRModel, bestSVMModel, bestRandomForestModel)
    val voteResult = modelVote.vote(testData)
    val voteAccurate = modelVote.accurate(voteResult)

    println("##################### Vote Result ########################")
    println("Model ROC = " + voteAccurate._1)
    println("Model PRC = " + voteAccurate._2)
    println("Model Correct Num = " + voteAccurate._3)
  }
}
