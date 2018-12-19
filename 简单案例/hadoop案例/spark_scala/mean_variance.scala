import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2018/4/13.
  */
object mean_variance {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("MeanVariance")
    val sc   = new SparkContext(conf)
    val data = sc.textFile("d://data/Irislengthmean.txt")
      .map(_.toDouble).map(line=>Vectors.dense(line))
    val summary = Statistics.colStats(data)
    println("鸢尾花萼片长均值为:"+summary.mean)
    println("鸢尾花萼片长方差为:"+summary.variance)
  }
}
