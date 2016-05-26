package util

import java.io.{File, PrintWriter}

/**
 * Created by WanEnFu on 16/5/26.
 */
class Prepocess {

  def prepoccessData(inputPath: String, outputPath: String): Unit = {
    val writer = new PrintWriter(new File(outputPath))
    val lines = scala.io.Source.fromFile(inputPath, "utf-8")
    var init = 0

    lines.getLines().foreach(e => {
      if(init == 0) {
        init = init + 1
      } else {
        val splitLine = e.toString.split(",")
        writer.print(splitLine(1)+ " 1:" + splitLine(0) + " 2:" + hoursTransform(splitLine(2)) + " 3:" + splitLine(3) + " 4:" + splitLine(4) + " 5:" + splitLine(5) +
          " 6:" + splitLine(6)  + " 7:" + splitLine(7) + " 8:" + splitLine(8)  + " 9:" + splitLine(9)  + " 10:" + splitLine(10) +
          " 11:" + splitLine(11) + " 12:" + splitLine(12) + " 13:" + splitLine(13) + " 14:" + splitLine(14) + " 15:" + splitLine(15) +
          " 16:" + splitLine(16) + " 17:" + splitLine(17) + " 18:" + splitLine(18) + " 19:" + splitLine(19) + " 20"  + splitLine(20) +
          " 21:" + splitLine(21)  + " 22:" + splitLine(22) + " 23:" + splitLine(23)
        )
        writer.print("\n")
      }
    })

    writer.close()
  }

  def hoursTransform(hours: String): Int = {
    val morn = 1
    val noon = 2
    val night = 3

    val hour = hours(6) + (7)
    var time = morn

    if(hour.equals("00")) {
      time = night
    } else if (hour.equals("01")) {
      time = night
    } else if (hour.equals("02")) {
      time = night
    } else if (hour.equals("03")) {
      time = night
    } else if (hour.equals("04")) {
      time = night
    } else if (hour.equals("05")) {
      time = night
    } else if (hour.equals("06")) {
      time = morn
    } else if (hour.equals("07")) {
      time = morn
    } else if (hour.equals("08")) {
      time = morn
    } else if (hour.equals("09")) {
      time = morn
    } else if (hour.equals("10")) {
      time = morn
    } else if (hour.equals("11")) {
      time = morn
    } else if (hour.equals("12")) {
      time = morn
    } else if (hour.equals("13")) {
      time = morn
    } else if (hour.equals("14")) {
      time = noon
    } else if (hour.equals("15")) {
      time = noon
    } else if (hour.equals("16")) {
      time = noon
    } else if (hour.equals("17")) {
      time = noon
    } else if (hour.equals("18")) {
      time = noon
    } else if (hour.equals("19")) {
      time = noon
    } else if (hour.equals("20")) {
      time = noon
    } else if (hour.equals("21")) {
      time = noon
    } else if (hour.equals("22")) {
      time = night
    } else if (hour.equals("23")) {
      time = night
    }

    time
  }


}
