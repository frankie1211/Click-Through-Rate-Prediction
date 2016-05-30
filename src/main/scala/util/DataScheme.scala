package util

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

/**
  * Created by benjamin658 on 2016/5/30.
  */
object DataScheme {
  val dataSchema = StructType(Array(
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
}
