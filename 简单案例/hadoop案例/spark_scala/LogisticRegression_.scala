import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2017/1/24.
  */
object LogisticRegression_ {
  val conf = new SparkConf().setAppName("LogisticRegreesion_").setMaster("local");
  val sc = new SparkContext(conf);

  def main(args: Array[String]): Unit = {
    //MLUtils Helper methods to load, save and pre-process data used in ML Lib.
    val data = MLUtils.loadLibSVMFile(sc,"D://data//sample_libsvm_data.txt");
    val model = LogisticRegressionWithSGD.train(data,50);//stepsize采取默认的1.0

    println(model.weights.size);//参数的数量
    println(model.weights)         //参数的值
    println(model.weights.toArray.filter(_!= 0 ).size)  //参数中不等于0的参数数量
  }
}
