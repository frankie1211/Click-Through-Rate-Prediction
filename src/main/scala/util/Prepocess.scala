package util

import java.io.{File, PrintWriter}

/**
 * Created by WanEnFu on 16/5/26.
 */
class Prepocess extends Serializable{

  def prepocessData(inputPath: String, outputPath: String): Unit = {
    val writer = new PrintWriter(new File(outputPath))
    val lines = scala.io.Source.fromFile(inputPath, "utf-8")
    var init = 0

    lines.getLines().foreach(e => {
      if(init == 0) {
        writer.print(e)
        writer.print("\n")
        init = init + 1
      } else {
        val splitLine = e.toString.split(",")
        val emptyString = splitLine.filter(s => s == "")
        if(splitLine.length == 24 && emptyString.length == 0) {
          writer.print(e)
          writer.print("\n")
        }
      }
    })

    writer.close()
  }

}
