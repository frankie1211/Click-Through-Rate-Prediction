package fs

import fs.core.FeatureSelection
import util.DataReader2

/**
 * Created by WanEnFu on 16/5/30.
 */
object Main {

  def main (args: Array[String]): Unit ={
    val filePath = "/Users/benjamin658/workspace/develop/small_filter.csv"
    val loadedData: DataReader2#InnerDataReader = new DataReader2().chain.readFile(filePath)
    val featureSelection: List[List[(Double, (String, Int))]] = new FeatureSelection(loadedData).selectFeature(10, 50)
    featureSelection.foreach(e=>println(e))
  }
}
