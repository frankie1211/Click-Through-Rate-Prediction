package fs.matrix

import org.apache.spark.mllib.linalg.{Vector, Vectors}

/**
 * Created by WanEnFu on 16/5/28.
 */
abstract class Matrix {

  final def makeVector(ts: Array[Double]): Vector = {
    val v: Vector = Vectors.dense(ts)
    v
  }

}
