import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2018/4/18.
  */
object IrisBayes {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("irisbayes").setMaster("local")
    val sc = new SparkContext(conf)
    val data = MLUtils.loadLabeledPoints(sc,"d://data/irisbayes.txt")
    val model = NaiveBayes.train(data,1.0)
    val test = Vectors.dense(6.7,2.5,5.8,1.8)
    val result = model.predict(test)
    println("该组特征数据所在的类别为"+result)
  }
}
