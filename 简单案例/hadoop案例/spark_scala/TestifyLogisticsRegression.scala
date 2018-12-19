import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

import scala.tools.cmd.gen.AnyVals.L

/**
  * Created by 2281444815 on 2017/1/24.
  */
object TestifyLogisticsRegression {
  val conf = new SparkConf().setAppName("TestifyLogisticRegreesion").setMaster("local");
  val sc = new SparkContext(conf);

  def main(args: Array[String]): Unit = {
    val data = MLUtils.loadLibSVMFile(sc,"D://data//sample_libsvm_data.txt");
    /*randomSplit
    该函数根据weights权重，将一个RDD切分成多个RDD。
    该权重参数为一个Double数组
    第二个参数为random的种子，基本可忽略。
    对数据进行分割，60%数据用于模型训练，40%数据用于模型验证
     */
    val splits = data.randomSplit(Array(0.6,0.4));
    val parsedData = splits(0);
    val parsedTest = splits(1);
    val model = LogisticRegressionWithSGD.train(parsedData,50);

    println(model.weights)

    val predictionAndLabels = parsedTest.map{
      case LabeledPoint(label,features) =>
        val prediction = model.predict(features)
        (prediction,label)
    }
    /**
      * ::Experimental::
      * MulticlassMetrics
      * Evaluator for multiclass classification.
      * @param predictionAndLabels an RDD of (prediction, label) pairs.
      */
    val metrics = new MulticlassMetrics(predictionAndLabels);
    val precision = metrics.precision;
    println("Precision = "+precision);

  }

}
