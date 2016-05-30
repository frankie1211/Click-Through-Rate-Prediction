package fs.matrix

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * Created by WanEnFu on 16/5/26.
 *
 * Coefficient Correlation Matrix
 *
 */
class CCM(mean: Vector, variance: Vector) extends Matrix {

  def makeLabeledPoint(vector: Vector, existFeatures: Array[Int]): LabeledPoint = {
    val label = vector(0)
    val permutations = getPermutations(vector.size-1, existFeatures: Array[Int])
    val correlations = calulateCorrelation(vector, permutations)
    val labeledPoint = LabeledPoint(label, makeVector(correlations))
    labeledPoint
  }

  private def getPermutations(vectorLength: Int, existFeatures: Array[Int]): List[(Int, Int)] = {
    val permutationsListBuffer = scala.collection.mutable.ListBuffer.empty[(Int, Int)]

    for(i <- 1 to vectorLength) {
      if(existFeatures(i) == 0){
        for(j <- i+1 to vectorLength) {
          if(existFeatures(j) == 0) {
            permutationsListBuffer.append(i -> j)
          }
        }
      }
    }

    permutationsListBuffer.toList
  }

  private def calulateCorrelation(vector: Vector, permutations: List[(Int, Int)]): Array[Double] = {
    val correlations = permutations.map(e => (vector(e._1)-mean(e._1))*(vector(e._2)-mean(e._2))/(variance(e._1)*variance(e._2)))
    correlations.toArray
  }

}
