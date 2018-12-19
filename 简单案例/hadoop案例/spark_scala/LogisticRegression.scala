import org.apache.spark.mllib.classification.{LogisticRegressionWithSGD, SVMWithSGD}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2017/1/24.
  */
object LogisticRegression {
  val conf = new SparkConf().setMaster("local").setAppName("LogisticRegression");
  val sc = new SparkContext(conf);

  def main(args: Array[String]): Unit = {
    val data = sc.textFile("D://data//logisticregression.txt");
    val parsedData = data.map{ line =>
      val parts = line.split(",")
      //注意这里即使x只有一个数据，也不可省去split(" ")方法
      LabeledPoint(parts(0).toDouble,   Vectors.dense(parts(1).split(" ").map(_.toDouble)));
    }.cache();

    val model = LogisticRegressionWithSGD.train(parsedData,50);
    val result = model.predict(Vectors.dense(16));
//    val modelsvm =SVMWithSGD.train(parsedData,20);
//    println("权重"+modelsvm.weights);
//    println("截距"+modelsvm.intercept);
    println(result);
  }
}
