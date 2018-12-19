import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.util.MLUtils

//import org.apache.spark.ml.classification.NaiveBayes
//import org.apache.spark.mllib.util.MLUtils
//import org.apache.spark.{SparkConf, SparkContext}
/**
  * Created by 2281444815 on 2017/2/7.
  */
object Bayes {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local").setAppName("Bayes");
    val sc = new SparkContext(conf);

    val data = MLUtils.loadLabeledPoints(sc, "D://data//bayes.txt");
    //这里我们采用系统提供的数据格式方法导入
    val model = NaiveBayes.train(data, 1.0) //注意别导错包啊，要不然错误要把你找死。。。

    model.labels.foreach(println);
    model.pi.foreach(println);

    val test = Vectors.dense(0, 0, 10);
    val result = model.predict(test);
    println("预测结果是" + result);
  }

}
