import util.Prepocess

/**
 * Created by WanEnFu on 16/5/26.
 *
 * 資料前處理程式, 轉換成 libsvm 格式
 * args(0): inputPath
 * args(1): outputPath
 *
 */
object PrepocessApp {

  def main(args: Array[String]): Unit = {
    val pre = new Prepocess()
    pre.prepoccessData(args(0), args(1))
  }
}