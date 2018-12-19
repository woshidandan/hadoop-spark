import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.util.MLUtils
/**
  * Created by 2281444815 on 2018/4/17.
  */
object iriskmeans {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("iriskmeans")
    val sc = new SparkContext(conf)
    val fileData = sc.textFile("d://data/kmeansdata.txt", 1)
    val parseData = fileData.map(record => Vectors.dense(record.split(" ").map(_.toDouble)))
    val numClusters = 3
    val numIterations = 20
    val model = KMeans.train(parseData,numClusters,numIterations)
    model.clusterCenters.foreach(println)
  }
}
