import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{SparkConf, SparkContext}

object iriscorrect {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("IrisCorr")
      .setMaster("local")
    val sc = new SparkContext(conf)
    val data1 = sc.textFile("d://data/petallength.txt")
      .flatMap(_.split(' '))
      .map(_.toDouble)
    val data2 = sc.textFile("d://data/petalwidth.txt")
      .flatMap(_.split(' '))
      .map(_.toDouble)
    val correlation: Double = Statistics.corr(data1,data2)
    println(correlation)
  }
}
