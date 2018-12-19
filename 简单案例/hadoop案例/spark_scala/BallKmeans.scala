import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
/**
  * Created by 2281444815 on 2016/11/19.
  */
object BallKmeans {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("BallKmeans").setMaster("local");
    val sc = new SparkContext(conf);
    val fileData = sc.textFile("D:\\bolldata.txt");
    //原数据中分隔符用"/t"会报错NumberFormatException: empty String，相当于把null=>double
    val parseData = fileData.map(record => Vectors.dense(record.split(" ").map(_.toDouble)));

    val dataModelNumber = 3;
    val dataModelTrainTimes = 200000;
    val model = KMeans.train(parseData, dataModelNumber, dataModelTrainTimes);
    //    println("Cluster centers:")
    //    for (c <- model.clusterCenters){
    //      println(" " + c.toString);
    //    }

    //计算误差
    val cost = model.computeCost(parseData)
    println("Within Set Sum of Squared Errors = " + cost)

   //打印结果
    val result = fileData.map {
      line =>
        val linevectore = Vectors.dense(line.split(" ").map(_.toDouble));
        val prediction = model.predict(linevectore);
        line + " " + prediction;
    }
    result.foreach(println)
    sc.stop();
  }
}
