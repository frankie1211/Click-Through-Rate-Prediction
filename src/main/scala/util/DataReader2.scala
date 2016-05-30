package util

import org.apache.spark.ml.feature.RFormula
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, DataFrame}

/**
  * Created by benjamin658 on 2016/5/30.
  */
class DataReader2() {
  private val sc = SparkInit.getSparkContext()
  private val sqlContext = SparkInit.getSqlContext()
  private val dataScheme = DataScheme.dataSchema
  private val empDf = sqlContext.createDataFrame(sc.emptyRDD[Row], DataScheme.dataSchema)
  val chain = new InnerDataReader(empDf)

  class InnerDataReader(dataFrame: DataFrame) {
    val df = dataFrame.cache()

    def readFile(filePath: String) = {
      val data = sqlContext.read
        .format("com.databricks.spark.csv")
        .schema(dataScheme)
        .option("header", "true")
        .load(filePath)

      new InnerDataReader(data)
    }

    def selectFeatures(targetFeatures: List[String]) = {
      val featureFormula = "click ~ " + UtilTools.genFeatureFormula(targetFeatures)
      val formula = new RFormula()
        .setFormula(featureFormula)
        .setFeaturesCol("features")
        .setLabelCol("label")

      if (targetFeatures.indexOf("hour") > -1) {
        val convertedDf = convertHours(df)
        val fitDf = formula.fit(convertedDf).transform(convertedDf)
        new InnerDataReader(fitDf)
      } else {
        val fitDf = formula.fit(df).transform(df)
        new InnerDataReader(fitDf)
      }
    }

    def getLabelPoint(): RDD[LabeledPoint] = {
      df.select("label", "features").map(row => {
        LabeledPoint(row.getDouble(0), row.getAs[Vector](1))
      })
    }

    private def convertHours(dataFrame: DataFrame): DataFrame = {
      val convertHour = sqlContext.createDataFrame(dataFrame.rdd.map(row => {
        Row(
          row.getString(0), row.getInt(1), UtilTools.hoursTransform(row.getInt(2)),
          row.getInt(3), row.getInt(4), row.getString(5), row.getString(6),
          row.getString(7), row.getString(8), row.getString(9), row.getString(10),
          row.getString(11), row.getString(12), row.getString(13), row.getInt(14),
          row.getInt(15), row.getInt(16), row.getInt(17), row.getInt(18),
          row.getInt(19), row.getInt(20), row.getInt(21), row.getInt(22),
          row.getInt(23)
        )
      }), dataFrame.schema)

      convertHour
    }
  }
}
