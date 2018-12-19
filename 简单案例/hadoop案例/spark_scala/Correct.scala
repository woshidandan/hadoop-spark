import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2016/11/28.
  *  皮尔逊相关系数代表两组数据的余弦分开程度，表示随着数据量的增加，两组数据的差别将增大。而
  *  斯皮尔曼相关系数则更注重两组数据的拟合程度，即两组数据随数据量增加而增长曲线不变。
  * Compute the Pearson correlation for the input RDDs.
  * Returns NaN if either vector has 0 variance.
  *
  * Note: the two input RDDs need to have the same number of partitions and the same number of
  * elements in each partition.
  *
  * @param x RDD[Double] of the same cardinality as y.
  * @param y RDD[Double] of the same cardinality as x.
  * @return A Double containing the Pearson correlation between the two input RDD[Double]s
  *
  @Since("1.1.0")
  def corr(x: RDD[Double], y: RDD[Double]): Double = Correlations.corr(x, y)
  */

object Correct {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Correct").setMaster("local");
    val sc = new SparkContext(conf);
    val rddX = sc.textFile("D:\\data\\xcorrect.txt")
      .flatMap(_.split(" "))
      .map(_.toDouble);
    val rddY = sc.textFile("D:\\data\\ycorrect.txt")
      .flatMap(_.split(" "))
      .map(_.toDouble);
    val rddZ =sc.textFile("D:\\data\\zcorrect.txt")
      .map(_.split(" ")
        .map(_.toDouble))     //注意此处
        .map(line =>Vectors.dense(line))
    rddZ.foreach(println)
//    val correctlation1: Double = Statistics.corr(rddX,rddY);//使用皮尔逊相关系数计算
//    val correctlation2: Double = Statistics.corr(rddX,rddY,"spearman");//使用斯皮尔曼相关系数计算
//    println("correctlation1:"+correctlation1);
//    println("correctlation2:"+correctlation2);
    println(Statistics.corr(rddZ,"spearman"))
  }
}
