import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by 2281444815 on 2017/1/22.
  */
object irisLinearRegression {
  val conf = new SparkConf().setMaster("local").setAppName("LinearRegression");
  val sc = new SparkContext(conf);

  def main(args: Array[String]): Unit = {
    val data = sc.textFile("D:\\data\\petallength.txt")
    val parsedData = data.map{line =>
      val parts = line.split(" ");
      LabeledPoint(parts(0).toDouble,   Vectors.dense(parts(1).split(" ").map(_.toDouble)));
    }.cache();
    val model = LinearRegressionWithSGD.train(parsedData,20,0.1);
    println("参数的值a为："+ model.weights);
    println("参数的值b为："+ model.intercept);
    val valuesAndPreds = parsedData.map { point =>{
      val prediction = model.predict(point.features)
      (point.label,prediction)}}
    val MSE = valuesAndPreds.map{
      case(v,p) => math.pow((v - p),2)}.mean()
    println("均方误差MSE为："+ MSE);
  }
}
