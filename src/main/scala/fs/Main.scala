package fs

import fs.core.FeatureSelection

/**
 * Created by WanEnFu on 16/5/30.
 */
object Main {

  def main (args: Array[String]): Unit ={
    val featureSelection = new FeatureSelection().selectFeature(10, 50)
    featureSelection.foreach(e=>println(e))
  }
}
