package fs.core

import fs.conf.Features
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
 * Created by WanEnFu on 16/5/28.
 */
class SVM(iterationsNum: Int) {

  def trainSVM(trainData: RDD[LabeledPoint]): SVMModel = {
    val model = SVMWithSGD.train(trainData, iterationsNum)
    model
  }

  def rank(existFeatures: Array[Int], weights: Array[Double]): (Array[Int], List[(Double, (String, Int))]) = {
    val featuresList = "label"::Features.featuresList // 加上 label 是因為 existFeatures 第一個元素是 label
    val featuresWithName = featuresList.zip(existFeatures)
    val featuresExist = featuresWithName.filter(e => e._2 == 1).map(tmp => tmp._1 -> featuresWithName.indexOf(tmp))

    val permutations = existFeaturesPermutation(featuresExist)
    val weightWithName = permutations.zip(weights)
    val weightSum = featuresExist.map(e => {
      val featureWeight = weightWithName.filter(x => x._1._1 == e._1 || x._1._2 == e._1).map(e => e._2)
      featureWeight.sum
    })

    val sort = weightSum.zip(featuresExist).sortWith(_._1 < _._1)
    existFeatures(sort(0)._2._2) = 0
    sort.drop(0)

    (existFeatures, sort)
  }

  private def existFeaturesPermutation(featuresExist: List[(String, Int)]): List[(String, String)] = {
    val permutationsListBuffer = scala.collection.mutable.ListBuffer.empty[(String, String)]

    for(i <- 0 until featuresExist.size) {
      for(j <- i until featuresExist.size) {
        permutationsListBuffer.append(featuresExist(i)._1 -> featuresExist(j)._1)
      }
    }

    permutationsListBuffer.toList
  }


}
