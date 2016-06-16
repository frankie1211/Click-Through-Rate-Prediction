package report

import java.io.{File, PrintWriter}

/**
 * Created by WanEnFu on 16/6/15.
 */
class Reporter(outputPath: String) {

  val writer = new PrintWriter(new File(outputPath))

  def createReport(voteAccurate: List[(Double, Double, Double, (Int, Int, Int, Int), String)]): Unit = {

    writer.println("Model,auROC,auPR,correctNum,tp,fp,tn,fn,accurate")
    voteAccurate.foreach(e => {
      writer.println(
        e._5 + "," + e._1 + "," + e._2 + "," + e._3 + "," +
          e._4._1 + "," + e._4._2 + "," + e._4._3 + "," + e._4._4 + "," +
          (e._1+e._2)/(e._4._1 + e._4._2 + e._4._3 + e._4._4)
      )
    })

  }

  def close(): Unit = {
    writer.close()
  }

}
