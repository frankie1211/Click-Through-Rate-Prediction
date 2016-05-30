package fs

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by WanEnFu on 16/5/25.
 *
 * A Supervised Feature Subset Selection Technique for Multivariate Time Series which is Corona.
 *
 * This project is using Spark to implement Corona.
 *
 *
 * args(0): startDate
 * args(1): endDate
 * args(2): number of SVM iteration
 * args(3): return number of ranks
 *
 */
object CoronaApp {

  def main(args: Array[String]): Unit = {

    // ========== Step.1 Spark Application ==========
    val conf = new SparkConf().setAppName("Corona Feature Selection")
    conf.setMaster("local[1]")
    val sc = new SparkContext(conf)
    // ==============================================

    // ========== Step.2 Corona Configuration ==========
    CoronaConfig.startDate = args(0)
    CoronaConfig.endDate = args(1)
    // ==============================================

    // ========== Step.3 Multivariate Time Series Matrix ==========
    val mts = new MTS
    val mysql = new Mysql
    val featuresList_ = CoronaConfig.featuresList_

    // 取得 feature 並做成 RDD
    val features = featuresList_.map(fElement => {
      val ts = Array(mysql.select(fElement)) // 取得報價
      val rowsRdd = sc.parallelize(ts, 1)
      rowsRdd
    })

    // 取得 target label 並做成 RDD
    val target = mysql.select(CoronaConfig.target)
    val labels = Array(mts.calculateLabel(target))
    val labelsRdd = sc.parallelize(labels, 1)

    // 將 lablel RDD 與 feature RDD 合併
    val rddUnion = features.foldLeft(labelsRdd){(r1, r2) => r1.union(r2)}
    val rddUnionT = mts.transposeRowMatrix(rddUnion) // 將 rowsRdd 轉置

    // 將 array 轉換成 vector, 並做成 RowMatrix
    val rddUnionTtoVector = rddUnionT.map(e => {
      val v: Vector = mts.makeVector(e.toArray)
      v
    })
    val rowMatrix = mts.makeRowMatrix(rddUnionTtoVector) // 建立 RowMatrix
    // ==============================================

    // ========== Repeat train SVM until rank satisfy ==========
    val rankNum = args(3).toInt
    var existFeatures = Array.fill(1)(CoronaConfig.featuresLength+1) // index:0 is label, then features.
    existFeatures(0) = 0 // label set 0.
    var result = List[(Double, (String, Int))]()

    while(rankNum == existFeatures.sum) {

      // ========== Step.4 Vectorized ==========
      val summary = rowMatrix.computeColumnSummaryStatistics()
      val ccm = new CCM(summary.mean, summary.variance)
      val labeledPoints = rowMatrix.rows.map(e => {
        ccm.makeLabeledPoint(e, existFeatures)
      })
      // ==============================================

      // ========== Step.5 SVM ==========
      val svm = new SVM(args(2).toInt)
      val model = svm.trainSVM(labeledPoints)
      val ranks = svm.rank(existFeatures, model.weights.toArray)
      result = ranks._2
      existFeatures = ranks._1
      // ==============================================
    }
    // ==============================================

    // ========== Rank Report ==========
    val reportCreateTime = System.currentTimeMillis()
    val reporter = new Reporter(args(0), args(1), reportCreateTime, CoronaConfig.target)
    reporter.createReport(result)
    // ==============================================

  }

}
