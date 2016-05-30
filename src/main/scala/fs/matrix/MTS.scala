package fs.matrix

import java.text.{DateFormat, SimpleDateFormat}

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD

/**
 * Created by WanEnFu on 16/5/25.
 *
 * MTS is Multivariate Time Series.
 *
 */
class MTS extends Matrix {

  def calculateLabel(target: Array[Double]): Array[Double] = {
    val sell = 1.0
    val buy = 2.0
    val sdf: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var index = 0
    var startTimeStamp = sdf.parse(CoronaConfig.startDate).getTime
    val endTimeStamp = sdf.parse(CoronaConfig.endDate).getTime - CoronaConfig.labelWindow * 1000
    val labelListBuffer = scala.collection.mutable.ListBuffer.empty[Double]

    while(startTimeStamp <= endTimeStamp) {
      var sum = 0.0

      for(i <- index+1 to index+CoronaConfig.labelWindow) {
        sum = sum + target(i) - target(index)
      }

      if(sum < 0.0) {
        labelListBuffer.append(sell)
      } else if (sum > 0.0) {
        labelListBuffer.append(buy)
      } else {
        labelListBuffer.append(sell) // 因為 spark 只支援 binary svm classification, 所以第三類 none 通通預設為 sell
      }

      index = index + 1
      startTimeStamp = startTimeStamp + 1000
    }

    labelListBuffer.toArray
  }

  def makeRowMatrix(rows: RDD[Vector]): RowMatrix = {
    val rowMatrix: RowMatrix = new RowMatrix(rows)
    rowMatrix
  }

  def transposeRowMatrix(rows: RDD[Array[Double]]): RDD[List[Double]] = {

    // Split the matrix into one number per line.
    val byColumnAndRow = rows.zipWithIndex.flatMap {
      case (row, rowIndex) => row.zipWithIndex.map {
        case (number, columnIndex) => columnIndex -> (rowIndex, number)
      }
    }
    // Build up the transposed matrix. Group and sort by column index first.
    val byColumn = byColumnAndRow.groupByKey.sortByKey().values
    // Then sort by row index.
    val transposed = byColumn.map {
      indexedRow => indexedRow.toList.sortBy(_._1).map(_._2)
    }

    transposed
  }

}
