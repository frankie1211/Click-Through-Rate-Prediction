package util

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.{Row, DataFrame}
import org.apache.spark.sql.types._
import org.apache.spark.ml.feature.RFormula

/**
  * Created by benjamin658 on 2016/5/26.
  */

class DataReader(filePath: String) {
  private val sc = SparkInit.getSparkContext()
  private val sqlContext = SparkInit.getSqlContext()
  private val dataSchema = StructType(Array(
    StructField("id", StringType, false),
    StructField("click", IntegerType, true),
    StructField("hour", IntegerType, true),
    StructField("C1", IntegerType, true),
    StructField("banner_pos", IntegerType, true),
    StructField("site_id", StringType, true),
    StructField("site_domain", StringType, true),
    StructField("site_category", StringType, true),
    StructField("app_id", StringType, true),
    StructField("app_domain", StringType, true),
    StructField("app_category", StringType, true),
    StructField("device_id", StringType, true),
    StructField("device_ip", StringType, true),
    StructField("device_model", StringType, true),
    StructField("device_type", IntegerType, true),
    StructField("device_conn_type", IntegerType, true),
    StructField("C14", IntegerType, true),
    StructField("C15", IntegerType, true),
    StructField("C16", IntegerType, true),
    StructField("C17", IntegerType, true),
    StructField("C18", IntegerType, true),
    StructField("C19", IntegerType, true),
    StructField("C20", IntegerType, true),
    StructField("C21", IntegerType, true)
  ))

  def readData() = {
    val originData = sqlContext.read
      .format("com.databricks.spark.csv")
      .schema(dataSchema)
      .option("header", "true")
      .load(filePath)

    val convertHour = sqlContext.createDataFrame(originData.rdd.map(row => {
      Row(
        row.getString(0), row.getInt(1), UtilTool.hoursTransform2(row.getInt(2)),
        row.getInt(3), row.getInt(4), row.getString(5), row.getString(6),
        row.getString(7), row.getString(8), row.getString(9), row.getString(10),
        row.getString(11), row.getString(12), row.getString(13), row.getInt(14),
        row.getInt(15), row.getInt(16), row.getInt(17), row.getInt(18),
        row.getInt(19), row.getInt(20), row.getInt(21), row.getInt(22),
        row.getInt(23)
      )
    }), originData.schema)

    new DataConverter(convertHour)
  }

  class DataConverter(originData: DataFrame) {
    def selectFeatures(targetFeatures: List[String]) = {
      val featureFormula = "click ~ " + genFeatureFormula(targetFeatures)
      val formula = new RFormula()
        .setFormula(featureFormula)
        .setFeaturesCol("features")
        .setLabelCol("label")
      val output = formula.fit(originData).transform(originData)
      new RDDBuilder(output)
    }

    private def genFeatureFormula(targetFeatures: List[String]) = {
      targetFeatures.mkString("+")
    }
  }

  class RDDBuilder(dataFrame: DataFrame) {
    def getLabelPoint() = {
      dataFrame.select("label", "features").map(row => LabeledPoint(row.getDouble(0), row.getAs[Vector](1)))
    }
  }

}

object UtilTool {
  def hoursTransform2(hours: Int): Int = {
    val hour = hours % 100
    if (hour <= 5 || hour >= 22) 3
    else if (hour >= 6 && hour <= 13) 1
    else 2
  }
}