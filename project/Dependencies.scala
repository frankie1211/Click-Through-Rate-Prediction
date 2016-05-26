import sbt._
object Version {
  val scalaTest = "2.2.4"
  val spark     = "1.6.1"
}

object Library {
  val scalaTest             = "org.scalatest"     %%    "scalatest"                    % Version.scalaTest
  val sparkCore             = "org.apache.spark"  %%    "spark-core"                   % Version.spark
  val sparkSql              = "org.apache.spark"  %%    "spark-sql"                    % Version.spark
  val sparkMlLib            = "org.apache.spark"  %%    "spark-mllib"                  % Version.spark
}

object Dependencies {

  import Library._

  val SparkLib = Seq(
    sparkCore,
    sparkSql,
    sparkMlLib,
    scalaTest % "test"
  )
}
