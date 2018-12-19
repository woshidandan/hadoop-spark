import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2017/2/9.
  */
object SVMDemo {
  val conf = new SparkConf().setAppName("SVM").setMaster("local");
  val sc = new SparkContext(conf);

  def main(args: Array[String]): Unit = {

    val data = sc.textFile("D:\\data\\svmdata.txt"); //注意不能少数据，否则val model = SVMWithSGD.train(parsedData, 50);报错

    val splits = data.randomSplit(Array(0.7, 0.3));
    val parsedData = splits(0).map { line =>
      val parts = line.split(",");
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(" ").map(_.toDouble)));
    }.cache();
    val parsedTest = splits(1).map { line =>
      val parts1 = line.split(",");
      LabeledPoint(parts1(0).toDouble, Vectors.dense(parts1(1).split(" ").map(_.toDouble)));
    }.cache();
    /*我们的数据也可以采取下面这种系统默认提供的一种录入数据的方式：
      val data = MLUtils.loadLibSVMFile(sc,"D://data//svmdata.txt");
        val predictionLabel = parsedTest.map { point =>{
          val prediction = model.predict(point.features)
          (point.label,prediction)
        }
        }
        这种方式下，我们的数据格式要变为：
        0 1:59 2:0 3:43.4 4:2 5:1
        具体数据格式我们可以参考spark源码目录下data文件中：sample_libsvm_data.txt文件的格式
        因为我怕这种方式的数据比较麻烦，所以换了一种。
     */

    val model = SVMWithSGD.train(parsedData, 50);
    val predictionLabel = parsedTest.map {
      case LabeledPoint(label, features) =>
        val prediction = model.predict(features)
        (prediction, label)
    }


    val metrics = new MulticlassMetrics(predictionLabel);
    val precision = metrics.precision;
    println("验证值为：" + precision);
    //评估我们模型的准确度，越接近1，则越好

    val patient = Vectors.dense(Array(50, 1, 74.0, 1, 1));
    //对该组数据进行预测，注意第三个数据必须为double型，否则报错
    val result = model.predict(patient);
    if (result == 1)
      println("患者的胃癌有几率转移");
    else
      println("患者的胃癌不会转移");
//    验证值为：0.18181818181818182
//    患者的胃癌有几率转移
//    1.0
  }
}
