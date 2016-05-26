name := "ClickThroughRate"

version := "1.0"

scalaVersion := "2.10.5"

assemblyJarName in assembly := "CTR.jar"

libraryDependencies ++= Dependencies.SparkLib
libraryDependencies += "com.databricks" % "spark-csv_2.10" % "1.4.0"

baseAssemblySettings
assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$") => MergeStrategy.discard
  case "log4j.properties" => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

test in assembly := {}