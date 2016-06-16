package models

import fs.core.FeatureSelection
import models.algorithm.{NaiveBayesAlgorithm, SVM, LogisticRegression}
import org.apache.log4j.LogManager
import org.apache.log4j.Level
import org.apache.spark.mllib.classification.{SVMWithSGD, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import report.Reporter
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
    val filePath = "/Users/benjamin658/workspace/develop/small_filter.csv";
    val loadedData: DataReader2#InnerDataReader = new DataReader2().chain.readFile(filePath)
    val hyperParameters = new FeatureSelection(loadedData).selectFeature(22, 100) // take hyperParameters
    hyperParameters.foreach(h => {
      val targetFeatures = h.map(e => e._2._1)
      println("#####################Start to load data########################")
      val Array(trainData, testData) = loadedData
        .selectFeatures(targetFeatures)
        .getLabelPoint()
        .randomSplit(Array(0.8, 0.2))

      val splitData = getValidationData(trainData)
      LogManager.getRootLogger.setLevel(Level.ERROR)

      println("##################### Start to train model ########################")

      println("##################### First Model : Logistic Regression ########################")
      val lr = new LogisticRegression
      val lrModels = lr.hyperParameterTuning(splitData, List(0), List(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9))
      val bestLrParameter = lr.findBestModel(lrModels)
      val lrModel = new LogisticRegressionWithLBFGS().setNumClasses(2).run(trainData)
      val bestLRModel = lrModel.clearThreshold().setThreshold(bestLrParameter._4._2)
      println("##################### First Model Done ########################")

      println("##################### Second Model : SVM ########################")
      val svm = new SVM
      val svmModels = svm.hyperParameterTuning(splitData, List(10, 20), List(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
      val bestSVMParameter = svm.findBestModel(svmModels)
      val svmModel = SVMWithSGD.train(trainData, bestSVMParameter._4._1)
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

      val bestSVMModel = svmModel.clearThreshold().setThreshold(min._1 + (max._1 - min._1) * bestSVMParameter._4._2)
      println("##################### Second Model Done ########################")

      println("##################### Third Model : Naive Bayes ########################")
      val naby = new NaiveBayesAlgorithm(trainData, testData)
      val bestNaby = naby.hyperParameterTuning(List((1.0, "multinomial")))
      val bestNabyParamList = bestNaby.getBestParmList()
      val bestNabyModel = bestNaby.trainBestModel(trainData)
      println("##################### Third Model Done ########################")

      println("##################### Start to vote ########################")
      val modelVote = new ModelVote(bestLRModel, bestSVMModel, bestNabyModel)
      val voteResult = modelVote.vote(testData)
      val voteAccurate = modelVote.accurate(voteResult)

      println("##################### Vote Result ########################")
      voteAccurate.foreach(e => {
        println("Model" + e._5 + "ROC = " + e._1)
        println("Model" + e._5 + "PRC = " + e._2)
        println("Model" + e._5 + "Correct Num = " + e._3)
      })

      println("##################### Create Report ########################")
      val reporter = new Reporter("/Users/benjamin658/workspace/develop/reportVariableNum" + h.size + ".csv")
      reporter.createReport(voteAccurate, bestLrParameter, bestSVMParameter, bestNabyParamList)
      reporter.close
    })

  }
}
