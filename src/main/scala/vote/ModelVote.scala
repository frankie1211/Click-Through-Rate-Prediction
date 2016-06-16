package vote

import org.apache.spark.mllib.classification.{LogisticRegressionModel, SVMModel}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.rdd.RDD

/**
  * Created by benjamin658 on 2016/6/6.
  */
class ModelVote(lrModel: LogisticRegressionModel, svmModel: SVMModel, rdfModel: RandomForestModel) extends Serializable {
  def vote(dataSet: RDD[LabeledPoint]) = {
    val labelAndPreds = dataSet.map(point => {
      val lrPredict = lrModel.predict(point.features)
      val svmPredict = svmModel.predict(point.features)
      val rdfPredict = rdfModel.trees.map(tree => tree.predict(point.features)).filter(_ > 0).size.toDouble / rdfModel.numTrees
      val finalPredict = if (lrPredict + svmPredict + rdfPredict < 2) 0 else 1

      (finalPredict.toDouble, point.label, lrPredict, svmPredict, rdfPredict)
    })

    labelAndPreds
  }

  def accurate(labelAndPreds: RDD[(Double, Double, Double, Double, Double)]) = {
    val voteMatrix = labelAndPreds.map(e => (e._1, e._2))
    val lrMatrix = labelAndPreds.map(e => (e._3, e._2))
    val svmMatrix = labelAndPreds.map(e => (e._4, e._2))
    val rdfMatrix = labelAndPreds.map(e => (e._5, e._2))
    val modelMatrixList = List((voteMatrix, "vote"), (lrMatrix, "lr"), (svmMatrix, "svm"), (rdfMatrix, "rdf"))

    val result = modelMatrixList.map(e => {
      val metrics = new BinaryClassificationMetrics(e._1)
      val auROC = metrics.areaUnderROC
      val auPRC = metrics.areaUnderPR
      val correctNum = labelAndPreds.filter(pair => pair._1 != pair._2).count()

      val tp = e._1.collect().filter(pair => pair._1 == 1.0 && pair._2 == 1.0).length
      val fp = e._1.collect().filter(pair => pair._1 == 1.0 && pair._2 == 0.0).length
      val tn = e._1.collect().filter(pair => pair._1 == 0.0 && pair._2 == 0.0).length
      val fn = e._1.collect().filter(pair => pair._1 == 0.0 && pair._2 == 1.0).length

      (auROC, auPRC, correctNum.toDouble, (tp, fp, tn, fn), e._2)
    })

    result
  }
}
