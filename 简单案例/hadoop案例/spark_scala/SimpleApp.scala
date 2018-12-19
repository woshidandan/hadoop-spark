/* SimpleApp.scala */
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object SimpleApp {
  def main(args: Array[String]) {
    val logFile = "hdfs://hadoop-spark:9000/spark/data/one" // Should be some file on your system
    val conf = new SparkConf()
      .setAppName("Simple Application")
      .setMaster("local")
     // .setMaster("spark://hadoop-spark:7077")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 2).cache()
    val numAs = logData.filter(line => line.contains("a")).count()
    val numBs = logData.filter(line => line.contains("b")).count()
    println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
    sc.stop()
  }
}