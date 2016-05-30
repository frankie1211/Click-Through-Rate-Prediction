package util

/**
  * Created by benjamin658 on 2016/5/30.
  */
object UtilTool {
  def hoursTransform(hours: Int): Int = {
    val hour = hours % 100
    if (hour <= 5 || hour >= 22) 3
    else if (hour >= 6 && hour <= 13) 1
    else 2
  }

  def genFeatureFormula(targetFeatures: List[String]) = {
    targetFeatures.mkString("+")
  }
}
