package fs.core

import fs.conf.Features
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import util.{DataReader2, SparkInit}

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

/**
  * Created by WanEnFu on 16/5/30.
  */
class FeatureSelection(loadedData: DataReader2#InnerDataReader ) {

  def selectFeature(rankNum: Int, iterationNum: Int): List[List[(Double, (String, Int))]] = {
    val sc = SparkInit.getSparkContext()
    val featuresList = Features.featuresList
    val svm = new SVM(iterationNum)

    println("========== Repeat train SVM until rank satisfy ==========")
    def trainFeatureListRecursive() = {
      val resultListBuffer: ListBuffer[List[(Double, (String, Int))]] = scala.collection.mutable.ListBuffer.empty[List[(Double, (String, Int))]]
      val data: RDD[LabeledPoint] = loadedData.selectFeatures(featuresList).getLabelPoint()
      val existFeatures: Array[Int] = 0 +: Array.fill[Int](featuresList.size + 1)(1)
      @tailrec
      def train(
        resultListBuffer: ListBuffer[List[(Double, (String, Int))]],
        data: RDD[LabeledPoint],
        existFeatures: Array[Int]
      ): ListBuffer[List[(Double, (String, Int))]] = {
        rankNum match {
          case rankNum: Int if (rankNum > existFeatures.sum) => resultListBuffer
          case rankNum: Int if (rankNum <= existFeatures.sum) => {
            val model = svm.trainSVM(data)
            val ranks = svm.rank(existFeatures, model.weights.toArray)
            val featureListWithLabel = ("label" :: Features.featuresList).zip(existFeatures)
            val featureFilter = featureListWithLabel.filter(e => e._2 == 1).map(e => e._1)
            resultListBuffer.append(ranks._2)
            train(resultListBuffer, loadedData.selectFeatures(featureFilter).getLabelPoint(), ranks._1)
          }
        }
      }

      train(resultListBuffer, data, existFeatures)
    }

    trainFeatureListRecursive.toList
  }
}
